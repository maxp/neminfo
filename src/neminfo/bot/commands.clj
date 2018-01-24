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

(defn user-wallets [uid])


(defn mosaic-fmt [data]
  (s/join "\n"
    (for [d data :let [m (:mosaicId d) q (:quantity d)]]
      (str "<code>" (:namespaceId m) ":" (:name m) " </code> " q))))
;

(defn balance [ctx addr]
  (let [alist (if addr 
                [addr] 
                (user-wallets (:uid ctx)))]
    (if alist 
      (doseq [a alist :let [res (nis/account-mosaic a)]]
        (prn "res:" res)
        (if-let [data (:data res)]
          (send-text (:apikey ctx) (:cid ctx) 
            (str "<b>" a "</b>\n" (mosaic-fmt data))
            "HTML")
          ;;
          (send-text (:apikey ctx) (:cid ctx) 
            (str "<b>" a "</b>\n" "<code>Error:</code> " (:message res))
            "HTML")))
      ;;
      (send-text (:apikey ctx) (:cid ctx) "No wallets to show."))))
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
