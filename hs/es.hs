#!/usr/bin/env runhaskell

{-# LANGUAGE OverloadedStrings #-}

import Data.Aeson
import Control.Monad
import Control.Applicative
import qualified Data.ByteString.Lazy.Char8 as BS
import Network.HTTP (simpleHTTP, getRequest, getResponseBody)

data Health = Health { getClusterName :: String
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

main :: IO ()
main = do
  json <- get "http://localhost:9200/_cluster/health"
  let (Just health) = decode (BS.pack json) :: Maybe Health
  putStrLn $ unwords [
    getClusterName health
    ,getStatus health
    ,show $ getNumberOfNodes health
    ,show $ getNumberOfDataNodes health
    ,show $ getActivePrimaryShards health
    ,show $ getActiveShards health
    ,show $ getRelocatingShards health
    ,show $ getInitializingShards health
    ,show $ getUnassignedShards health
    ]

get :: String -> IO String
get url = getResponseBody =<< simpleHTTP (getRequest url)
