(ns example
  "Example app that uses cljpages.core tag helpers to build a page."
  (:require [cljpages.core :as h]))

(def users
  [{:name "Ann" :age 22 :vip true}
   {:name "Boris" :age 31 :vip false}
   {:name "Chen" :age 28 :vip true}])

(def products
  [{:name "Ball"   :price 15.99 :in? true  :tags ["sport" "kids"]}
   {:name "Shoes"  :price 79.00 :in? false :tags ["sport" "fashion"]}
   {:name "Bottle" :price 9.90  :in? true  :tags ["health"]}])

(def total (reduce + 0 (map :price products)))

(def page
  (h/html5 {:lang "en"}
    (h/head
      (h/meta-charset "utf-8")
      (h/title* "CljPages — example")
      (h/style "body{font-family:system-ui;margin:2rem} .vip{border:1px solid #888;padding:.1rem .4rem;border-radius:.3rem;font-size:.8rem} table{border-collapse:collapse;margin-top:1rem} th,td{border:1px solid #333;padding:.4rem .8rem} .tag{border:1px solid #aaa;border-radius:.35rem;padding:.05rem .3rem;margin-right:.3rem;font-size:.8rem} .card{border:1px solid #ddd;border-radius:.6rem;padding:1rem;margin:.6rem 0;box-shadow:0 1px 2px rgba(0,0,0,.04)}"))
    (h/body
      (h/header (h/h1 "Tag-only HTML builders"))

      (h/section
        (h/h2 "Users list (plain Clojure `for`/`when`)")
        (h/ul
          (for [u users]
            (h/li (h/strong (:name u)) " — " (:age u) " years old"
                  (when (:vip u) (h/span {:class "vip"} " VIP"))))))

      (h/section
        (h/h2 "Products table + totals")
        (h/p "Total value: " (format "%.2f" (double total)))
        (h/table
          (h/thead (h/tr (h/th "Name") (h/th "Price") (h/th "In stock?") (h/th "Tags")))
          (h/tbody
            (for [p products]
              (h/tr
                (h/td (:name p))
                (h/td (:price p))
                (h/td (if (:in? p) "Yes" "No"))
                (h/td (for [t (:tags p)] (h/span {:class "tag"} t))))))))

      (h/section
        (h/h2 "Void tags & attributes")
        (h/p "Image below uses a void tag:")
        (h/img {:src "https://upload.wikimedia.org/wikipedia/commons/6/60/Soccer_Field_-_empty.png"
                :alt "Sample pitch" :width "320"})
        (h/br)
        (h/small "Alt: Sample pitch"))

      (h/footer (h/small "Generated with CljPages — tag-only core")))))

(defn -main [& _]
  (h/emit! "out/index.html" page)
  (println "OK → out/index.html"))