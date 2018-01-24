(ns neminfo.nis.core
  (:require
    [clojure.string :as s]
    [clj-http.client :as http]
    [cheshire.core :refer [parse-string]]
    ;    
    [mlib.conf :refer [conf]]
    [mlib.log :refer [debug info warn]]))
;


(defn cleanup-addr [addr]
  (s/replace (str addr) #"[ \-]" ""))
;

(defn api-node []
  (let [nodes (-> conf :nem :nodes)]
    (get nodes (rand-int (count nodes)))))
;

(defn safe-json [s]
  (try
    (parse-string s true)
    (catch Exception ignore
      s)))
;

(defn get-api [url path params]
  (try
    (let [tout (-> conf :nem :timeout)
          {body :body status :status}
          (http/get (str url path)
            { :content-type :json
              :query-params params
              :throw-exceptions false
              :socket-timeout tout
              :conn-timeout tout})
          res (safe-json body)]
      (if (= 200 status)
        res
        (merge {:error "http" :status status} res)))
    (catch Exception e
      {:error "socket" :message (.getMessage e)})))
;

(defn heartbeat [url]
  (get-api url "/heartbeat" nil))
;

(defn chain-height [url]
  (get-api url "/chain/height" nil))
;

(defn account-mosaic [addr]
  (let [address (cleanup-addr addr)
        rc (get-api (api-node) "/account/mosaic/owned" {:address address})]
    (prn "rc:" rc)
    rc))
;

;;.
