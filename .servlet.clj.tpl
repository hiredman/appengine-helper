(ns NAME.servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use compojure.html compojure.http))

(defroutes NAME
  (GET "/*" (html [:h1 "Hello World"])))

(defservice NAME)
