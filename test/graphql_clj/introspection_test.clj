(ns graphql-clj.introspection-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [graphql-clj.type :as type]
            [graphql-clj.parser :as parser]
            [graphql-clj.resolver :as resolver]
            [graphql-clj.executor :as executor]))

(def simple-user-schema
  "type User {
  name: String
  nickname: String
  son: User
  friends: [User]
}

type QueryRoot {
  user: User
}

schema {
  query: QueryRoot
}")

(defn- create-test-schema
  [type-spec]
  (-> type-spec
      (parser/parse)
      (type/create-schema)))


(deftest test-schema-introspection
  (let [simple-schema (create-test-schema simple-user-schema)
        introspection-schema (create-test-schema (slurp (io/resource "introspection.schema")))
        schema (type/inject-introspection-schema simple-schema introspection-schema)
        resolver-fn nil
        context nil
        query "query { __schema { types {name kind} }}"
        result (executor/execute context schema resolver-fn query)]
    (is (not (nil? (:data result))))))
