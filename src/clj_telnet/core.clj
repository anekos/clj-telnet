(ns clj-telnet.core
  (:gen-class)
  (:import
    [org.apache.commons.net.telnet TelnetClient]
    [java.net InetSocketAddress Socket]
    [java.io PrintStream PrintWriter]))

(defn get-telnet
  "returns a telnetclient given server-ip as String and port as int"
  ([^String server-ip ^Integer port]
   ;test if server will connect on port
    (. (new java.net.Socket) connect
       (new java.net.InetSocketAddress server-ip port) 1000)
    (let [tc (TelnetClient.)]
      (.connect tc server-ip port)
      (.setKeepAlive tc true)
      tc))
  ([^String server-ip]
    (get-telnet server-ip 23)))

(defn kill-telnet
  "disconnects telnet-client"
  [^TelnetClient telnet-client]
  (.disconnect telnet-client))

(defn read-until
  "reads the input stream of a telnet client till it finds pattern"
  [^TelnetClient telnet ^String pattern]
  (let [in (.getInputStream telnet)]
    (loop [result ""]
      (let [s (char (.read in))]
        (if (= s (last pattern))
          (if (clojure.string/ends-with? (str result s) pattern)
            (str result s)
            (recur (str result s)))
          (recur (str result s)))))))

(defn write
  "writes to the output stream of a telnet client"
  [^TelnetClient telnet ^String s]
  (let [out (PrintStream. (.getOutputStream telnet))]
    (doto out
      (.println s)
      (.flush))))