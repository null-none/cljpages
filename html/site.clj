(ns site
  "Entry point that collects pages and emits the static site into `public/`."
  (:require
    [cljpages.core :as h]
    [pages.home :as home]
    [pages.users :as users]
    [pages.products :as products]
    [pages.blog :as blog]))

(def pages
  "Nested map of relative paths to page hiccup trees.
   - Keys may be strings or keywords.
   - Maps represent directories, leaves represent files."
  {"index.html" (home/page)
   "users.html" (users/page)
   "products.html" (products/page)
   "blog.html" (blog/post-1)})

(defn- count-leaves
  "Count how many HTML files will be produced from the nested `pages` map."
  [node]
  (cond
    (map? node) (reduce + 0 (map (fn [[_ v]] (count-leaves v)) node))
    :else 1))

(defn -main
  "Build the demo site into the `public/` directory.
   Usage from command line:

     clj -M:папка
  "
  [& _args]
  (let [target "public"
        _      (h/emit-batch! target pages)
        n      (count-leaves pages)]
    (println "OK →" target "(" n "files generated )")))
