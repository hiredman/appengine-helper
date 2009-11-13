(ns hiredman.datastore
  (:import (com.google.appengine.api.datastore DatastoreServiceFactory Query Query$FilterOperator Entity)))

(def #^{:private true} datastore-service (DatastoreServiceFactory/getDatastoreService)) 

(defn- entity->map [entity]
  (with-meta
    (into {} (map #(vector (keyword (key %)) (val %)) (.getProperties entity)))
    {:kind (keyword (.getKind entity))
     :key (.getKey entity)
     :entity entity}))

(defn create [map]
  (let [kind (name (:kind (meta map)))
        entity (Entity. kind)]
    (doseq [[name- value] map]
      (.setProperty entity (name name-) value))
    (.put datastore-service entity)
    (entity->map entity)))

(defn delete [map]
  (.delete datastore-service [(:key (meta map))])
  nil)

(defn update [map]
  (entity->map (reduce #(.setProperty %1 (name (first %2)) (second %2)) (:entity (meta map)) map)))

(defn exec [query]
  (-> query (#(.prepare datastore-service %)) .asIterable ((partial map entity->map))))

(def #^{:private true} operators
  {'= Query$FilterOperator/EQUAL
   '> Query$FilterOperator/GREATER_THAN
   '>= Query$FilterOperator/GREATER_THAN_OR_EQUAL
   '< Query$FilterOperator/LESS_THAN
   '<= Query$FilterOperator/LESS_THAN_OR_EQUAL})

(defmacro qfilter [[op x y] query]
  (let [[property-name value] (if (keyword? x) [(name x) y] [(name y) x])]
    `(doto query
       (.addFilter ~property-name
                   ~(operators op ::NO_SUCH_OPERATOR)
                   ~value))))

(defn query [kind]
  (Query. (name kind)))
