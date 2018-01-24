
(ns mlib.mdb.conn
  (:require
    [monger.json]
    [monger.joda-time]
    [monger.core :as mg]
    [mlib.conf :refer [conf]])
  (:import
    [org.joda.time DateTimeZone]
    [com.mongodb WriteConcern]))
;


(defn connect [cnf]
  (prn cnf)
  (->
    (:tz conf "Asia/Irkutsk")
    DateTimeZone/forID
    DateTimeZone/setDefault)
  (let [mdb (mg/connect-via-uri (:url cnf))]
    (mg/set-default-write-concern! WriteConcern/FSYNC_SAFE)
    mdb))
;

(defn disconnect [mdb]
  (mg/disconnect (:conn mdb)))
;


(comment

  ;; example
  (defstate mongodb
    :start
      (connect (-> conf :mongodb))
    :stop
      (disconnect mongodb))

  (defn dbc []
    (:db mongodb)))
;

;;.
