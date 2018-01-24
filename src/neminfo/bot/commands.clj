(ns neminfo.bot.commands
  (:import java.net.URL)
  (:require
    [clojure.string :as s]
;    [mlib.conf :refer [conf]]
    [mlib.tg.core :refer [send-text]]
    [mlib.log :refer [debug info warn]]
    ;
    [neminfo.build :refer [build]]
    [neminfo.nis.core :as nis]))
;


(defn balance [ctx addr]
  (send-text (:apikey ctx) (:cid ctx) "balance..."))
;

(defn nodes [ctx nodes]
  (doseq [n nodes]
    (let [status (nis/heartbeat n)
          height (:height (nis/chain-height n))
          u (URL. n)]
      (send-text (:apikey ctx) (:cid ctx)
        (str 
          "<b>NIS:</b> " (.getHost u) ":" (.getPort u) "\n"
          "status: " status "\n" 
          "chain height: " height)
        "HTML"))))
;          

(def HELP_TEXT
  (str
    "<b>NEM Info bot</b>\n" "version: " (:version build) "

command list:
/balance [ADDRESS] - show your wallet info
/watch ADDRESS - add address to watch list
<code>/remove</code>
<code>/nodes</code>
<code>/help</code>
  "))

(defn help [ctx]
  (send-text (:apikey ctx) (:cid ctx) HELP_TEXT "HTML"))
;

;;.
