(ns es.test.command.shards
  (:require [clojure.test :refer :all]
            [es.command.shards :as shards]))

(def wikis
  [["wiki" "3" "r" "STARTED" 429017254 "409.1mb" 28761 "Slade, Frederick"]
   ["wiki" "3" "p" "STARTED" 429013649 "409.1mb" 28761 "Cannonball I"]
   ["wiki" "3" "r" "STARTED" 429019084 "409.1mb" 28761 "Luichow, Chan"]
   ["wiki" "2" "r" "STARTED" 426738791 "406.9mb" 28718 "Slade, Frederick"]
   ["wiki" "2" "p" "STARTED" 426734771 "406.9mb" 28718 "Cannonball I"]
   ["wiki" "2" "r" "STARTED" 426734751 "406.9mb" 28718 "Eric the Red"]
   ["wiki" "4" "r" "STARTED" 430611290 "410.6mb" 28819 "Slade, Frederick"]
   ["wiki" "4" "p" "STARTED" 430608757 "410.6mb" 28819 "Cannonball I"]
   ["wiki" "4" "r" "STARTED" 430612693 "410.6mb" 28819 "Luichow, Chan"]
   ["wiki" "0" "p" "STARTED" 424543981 "404.8mb" 28826 "Cannonball I"]
   ["wiki" "0" "r" "STARTED" 424547838 "404.8mb" 28826 "Luichow, Chan"]
   ["wiki" "0" "r" "STARTED" 424543961 "404.8mb" 28826 "Eric the Red"]
   ["wiki" "1" "p" "STARTED" 423851451 "404.2mb" 28576 "Slade, Frederick"]
   ["wiki" "1" "r" "STARTED" 423845459 "404.2mb" 28576 "Eric the Red"]
   ["wiki" "1" "r" "STARTED" 423848976 "404.2mb" 28576 "Luichow, Chan"]])

(deftest shards
  (is (= wikis (shards/go ["wik*"] {:url "local:"}))))
