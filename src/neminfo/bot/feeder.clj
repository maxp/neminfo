(ns neminfo.bot.feeder
  (:require
    [clojure.core.async :refer [chan <!! close! thread]]
    [mount.core :refer [defstate]]
    [mlib.conf :refer [conf]]
    [mlib.log :refer [debug info warn]]    
    [mlib.tg.core :refer [send-text]]
    [mlib.tg.poller :as poller]
    [neminfo.bot.route :as r]))
;


(defstate inbound-channel
  :start 
    (chan)
  :stop
    (close! inbound-channel))
;

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
        :message        :>> r/message
        :edited_message :>> r/message
        :callback_query :>> r/callback
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
      (when-let [new-state (loop-fn state)]
        (recur new-state)))
    (info "thread-loop: end.")))
;

(defstate inbound-thread
  :start
    (thread-loop process-inbound inbound-channel)
  :stop
    (when inbound-thread
      (close! inbound-channel)))
;

;;.
