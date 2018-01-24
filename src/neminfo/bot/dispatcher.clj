(ns neminfo.bot.dispatcher
  (:require
    [clojure.core.async :refer [chan <!! close! thread]]
    [mount.core :refer [defstate]]
    [mlib.conf :refer [conf]]
    [mlib.log :refer [debug info warn]]    
    [mlib.tg.core :refer [send-text]]
    [mlib.tg.poller :as poller]
    [neminfo.bot.handlers :as h]))
;


(def inbound-channel (chan))

(defstate poller-thread
  :start 
    (poller/start (:tg conf) inbound-channel)
  :stop  
    (when poller-thread 
      (poller/stop poller-thread)))
;


(defn process-inbound [ch]
  (when-let [upd (<!! ch)]
    (try
      (condp #(%1 %2) upd
        :message        :>> h/message
        :edited_message :>> h/message
        :callback_query :>> h/callback
        ;:inline_query   :>> h/inline
        ;:chosen_inline_result nil
        (debug "process-inbound: unexpected update -" upd))
      (catch Exception e
        (warn "process-inbound:" upd (or (.getMessage e) e))))
    ch))
;

(defn thread-loop [loop-fn initial-state]
  (thread 
    (loop [state initial-state]
      (if-let [new-state (loop-fn state)]
        (recur new-state)
        (info "thread-loop: end.")))))
;

(defstate inbound-thread
  :start
    (thread-loop process-inbound inbound-channel)
  :stop
    (when inbound-thread
      (close! inbound-channel)))
;

;;.
