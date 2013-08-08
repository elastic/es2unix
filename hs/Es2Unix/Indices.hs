{-# LANGUAGE OverloadedStrings #-}

module Es2Unix.Indices where

import Control.Monad
import Control.Applicative
import Data.Aeson
import Data.Map (Map, toList) 
import Data.Text (Text, unpack)
import qualified Data.ByteString.Lazy.Char8 as BS

newtype Indices = Indices (Map String IndexHealth)
                deriving (Show)

data Health = Health
              { status :: Text
              , indices :: Indices
              } deriving (Show)

data IndexHealth = IndexHealth
                   { indexStatus :: Text
                   } deriving (Show)

instance FromJSON Indices where
  parseJSON v = Indices <$> parseJSON v

instance FromJSON Health where
  parseJSON j = do
    o <- parseJSON j
    Health <$>
      o .: "status" <*>
      o .: "indices"

instance FromJSON IndexHealth where
  parseJSON j = do
    o <- parseJSON j
    IndexHealth <$> o .: "status"

getIndices :: BS.ByteString -> Maybe Indices
getIndices j =
  case decode j :: Maybe Health of
    (Just health) -> Just (indices health)
    Nothing       -> Nothing

p :: (String, IndexHealth) -> IO ()
p (k, ih) = putStrLn $ unpack (indexStatus ih) ++ " " ++ k

main :: IO ()
main = do
  json <- BS.getContents
  case getIndices json of
    Just (Indices m) -> mapM_ p (toList m)
    Nothing -> putStrLn "invalid json"

