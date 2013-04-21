#!/usr/bin/env runhaskell

import Data.Aeson
import qualified Data.ByteString.Lazy.Char8 as BS
import Network.HTTP (simpleHTTP, getRequest, getResponseBody)
import Es2Unix.Health

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
