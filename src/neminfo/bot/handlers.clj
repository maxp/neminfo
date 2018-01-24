(ns neminfo.bot.handlers
  (:require
    [mlib.conf :refer [conf]]
    [mlib.tg.core :refer [send-text]]
    [mlib.log :refer [debug info warn]]))
;




(defn message [msg]
  (debug "message:" msg))
;

(defn callback [msg]
  (debug "callback:" msg))
;

;;.
