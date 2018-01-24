
(ns neminfo.main
  (:gen-class)
  (:require
    [mount.core :refer [start-with-args]]
    [mlib.conf :refer [conf]]
    [mlib.core :refer [edn-read]]
    [mlib.log :refer [info warn]]
    ;
    [neminfo.build :refer [build]]
    [neminfo.bot.feeder]))
;

(defn -main [& args]
  (if-let [rc (edn-read (first args))]
    (do
      (info (:name build) (:version build) (:timestamp build))
      (start-with-args rc))
    (warn "config profile must be in parameters!")))
  ;

;;.
