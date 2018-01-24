(ns neminfo.bot.route
  (:require
    [clojure.string :as s]
    [mlib.conf :refer [conf]]
;    [mlib.tg.core :refer [send-text]]
    [mlib.log :refer [debug info warn]]
    [neminfo.bot.commands :as h]))
;


(def RE_COMMAND #"^(/[A-Za-z0-9]+)([ _](.+))?$")

(defn split-command [text]
  (when-let [[_ cmd _ tail] (re-matches RE_COMMAND text)]
    (if tail
      (into [cmd] (s/split tail #"[ _]"))
      [cmd])))
;

(defn message [msg]
  (debug "message:" msg)
  (let [text (:text msg)
        [cmd param & other] (split-command text)
        ctx { :apikey (-> conf :tg :apikey)
              :cid (-> msg :chat :id)
              :uid (-> msg :from :id)}]
    ;
    (condp = (s/lower-case cmd)
      "/balance"  (h/balance ctx text)  ;; FIX!!!
      "/nodes"    (h/nodes ctx (-> conf :nem :nodes))
      "/watch"    (h/help ctx)
      "/remove"   (h/help ctx)
      "/help"     (h/help ctx)
      nil)))
;

(defn callback [msg]
  (debug "callback:" msg))
;

;;.
