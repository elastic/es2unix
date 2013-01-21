(ns es.data.indices
  (:require [es.data.cluster :as cluster]
            [es.http :as http]
            [es.util :as util]
            [slingshot.slingshot :refer [throw+]]))

(defn index-slice
  ([url indices endpoint]
     (let [lst (util/comma-list indices)
           lst (if (pos? (count lst)) (str "/" lst) "")]
       (http/get (str url lst endpoint)))))

(defn status
  ([url]
     (status url []))
  ([url indices]
     (index-slice url indices "/_status")))

(defn stats
  ([url]
     (status url []))
  ([url indices]
     (index-slice url indices "/_stats")))

(defn replica-totals [url indices]
  (->> (for [[nam data] (:indices (status url indices))]
         [nam (apply
               merge-with +
               (for [[id replicas] (:shards data)
                     replica replicas]
                 {:bytes (-> replica :index :size_in_bytes)
                  :docs (-> replica :docs :num_docs)}))])
       (into {})))

(defn indices
  ([url]
     (indices url []))
  ([url indices]
     (util/merge-transpose
      {:health (cluster/health url indices)}
      {:status (:indices (status url indices))}
      {:stats (get-in (stats url indices) [:_all :indices])})))

(defn make-replica-key [routing]
  [
   (:index routing)
   (:shard routing)
   (:primary routing)
   (:node routing)
   ])

(defn shards
  ([url]
     (shards url []))
  ([url indices]
     (->> (for [[idxname index] (:indices (status url indices))
                [shard replicas] (:shards index)
                replica replicas]
            [(make-replica-key (:routing replica)) replica])
          (into {}))))










{:ok true
 :_shards {:total 4
           :successful 4
           :failed 0}
 :_all {:primaries {:docs {:count 2319799
                           :deleted 0}
                    :store {:size "10.8gb"
                            :size_in_bytes 11687875683
                            :throttle_time "0s"
                            :throttle_time_in_millis 0}
                    :indexing {:index_total 0
                               :index_time "0s"
                               :index_time_in_millis 0
                               :index_current 0
                               :delete_total 0
                               :delete_time "0s"
                               :delete_time_in_millis 0
                               :delete_current 0}
                    :get {:missing_total 0
                          :time_in_millis 0
                          :total 0
                          :current 0
                          :missing_time "0s"
                          :exists_time "0s"
                          :exists_total 0
                          :time "0s"
                          :exists_time_in_millis 0
                          :missing_time_in_millis 0}
                    :search {:query_total 0
                             :query_time "0s"
                             :query_time_in_millis 0
                             :query_current 0
                             :fetch_total 0
                             :fetch_time "0s"
                             :fetch_time_in_millis 0
                             :fetch_current 0}}
        :total {:docs {:count 4639598
                       :deleted 0}
                :store {:size "21.7gb"
                        :size_in_bytes 23375743891
                        :throttle_time "0s"
                        :throttle_time_in_millis 0}
                :indexing {:index_total 0
                           :index_time "0s"
                           :index_time_in_millis 0
                           :index_current 0
                           :delete_total 0
                           :delete_time "0s"
                           :delete_time_in_millis 0
                           :delete_current 0}
                :get {:missing_total 0
                      :time_in_millis 0
                      :total 0
                      :current 0
                      :missing_time "0s"
                      :exists_time "0s"
                      :exists_total 0
                      :time "0s"
                      :exists_time_in_millis 0
                      :missing_time_in_millis 0}
                :search {:query_total 0
                         :query_time "0s"
                         :query_time_in_millis 0
                         :query_current 0
                         :fetch_total 0
                         :fetch_time "0s"
                         :fetch_time_in_millis 0
                         :fetch_current 0}}
        :indices {:wiki {:primaries {:docs {:count 2319799
                                            :deleted 0}
                                     :store {:size "10.8gb"
                                             :size_in_bytes 11687875683
                                             :throttle_time "0s"
                                             :throttle_time_in_millis 0}
                                     :indexing {:index_total 0
                                                :index_time "0s"
                                                :index_time_in_millis 0
                                                :index_current 0
                                                :delete_total 0
                                                :delete_time "0s"
                                                :delete_time_in_millis 0
                                                :delete_current 0}
                                     :get {:missing_total 0
                                           :time_in_millis 0
                                           :total 0
                                           :current 0
                                           :missing_time "0s"
                                           :exists_time "0s"
                                           :exists_total 0
                                           :time "0s"
                                           :exists_time_in_millis 0
                                           :missing_time_in_millis 0}
                                     :search {:query_total 0
                                              :query_time "0s"
                                              :query_time_in_millis 0
                                              :query_current 0
                                              :fetch_total 0
                                              :fetch_time "0s"
                                              :fetch_time_in_millis 0
                                              :fetch_current 0}}
                         :total {:docs {:count 4639598
                                        :deleted 0}
                                 :store {:size "21.7gb"
                                         :size_in_bytes 23375743891
                                         :throttle_time "0s"
                                         :throttle_time_in_millis 0}
                                 :indexing {:index_total 0
                                            :index_time "0s"
                                            :index_time_in_millis 0
                                            :index_current 0
                                            :delete_total 0
                                            :delete_time "0s"
                                            :delete_time_in_millis 0
                                            :delete_current 0}
                                 :get {:missing_total 0
                                       :time_in_millis 0
                                       :total 0
                                       :current 0
                                       :missing_time "0s"
                                       :exists_time "0s"
                                       :exists_total 0
                                       :time "0s"
                                       :exists_time_in_millis 0
                                       :missing_time_in_millis 0}
                                 :search {:query_total 0
                                          :query_time "0s"
                                          :query_time_in_millis 0
                                          :query_current 0
                                          :fetch_total 0
                                          :fetch_time "0s"
                                          :fetch_time_in_millis 0
                                          :fetch_current 0}}}}}}
