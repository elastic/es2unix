{-# LANGUAGE OverloadedStrings #-}

module Es2Unix.Health where

import Control.Applicative
import Control.Monad
import Data.Aeson

data Health =
  Health { getClusterName :: String
         , getStatus :: String
         , getNumberOfNodes :: Integer
         , getNumberOfDataNodes :: Integer
         , getActivePrimaryShards :: Integer
         , getActiveShards :: Integer
         , getRelocatingShards :: Integer
         , getInitializingShards :: Integer
         , getUnassignedShards :: Integer
         } deriving (Show)

instance FromJSON Health where
  parseJSON (Object v) = Health <$>
                         (v .: "cluster_name") <*>
                         (v .: "status") <*>
                         (v .: "number_of_nodes") <*>
                         (v .: "number_of_data_nodes") <*>
                         (v .: "active_primary_shards") <*>
                         (v .: "active_shards") <*>
                         (v .: "relocating_shards") <*>
                         (v .: "initializing_shards") <*>
                         (v .: "unassigned_shards")
  parseJSON _ = mzero

