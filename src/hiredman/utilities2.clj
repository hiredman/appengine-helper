(ns hiredman.utilities2
  (:import (java.util Properties)
           (java.io File FileInputStream FileWriter PrintWriter OutputStreamWriter BufferedReader InputStreamReader)))

(defmacro load-properties
  "macroexpand/compile time loading of a properties file into a clojure map"
  [filename]
  (let [p (Properties.)
        is (-> filename File. FileInputStream.)]
    (.load p is)
    (into {} p)))


(defmacro cron [file stuff]
  (let [x {:tag :cronentries :attrs nil :content stuff}
        x (update-in x
            [:content]
            (partial map
              (fn [x]
                {:tag :cron :attrs nil
                 :content [{:tag :url :attrs nil
                            :content [(:url x)]}
                           {:tag :schedule :attrs nil
                            :content [(:schedule x)]}
                           {:tag :description :attrs nil
                            :content [(:desc x)]}
                           {:tag :timezone :attrs nil
                            :content [(:tz x)]}]})))]
    (with-open [f (-> file File. FileWriter. PrintWriter.)]
      (.println f (with-out-str (clojure.xml/emit x))))))

(defn juxt [& fns]
  (fn [& args]
    (vec (map #(apply % args) fns))))
