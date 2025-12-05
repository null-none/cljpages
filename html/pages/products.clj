(ns pages.products
  "Products page definition."
  (:require [cljpages.core :as h]))

(def products
  [{:name "Ball"   :price 15.99 :in? true  :tags ["sport" "kids"]}
   {:name "Shoes"  :price 79.00 :in? false :tags ["sport" "fashion"]}
   {:name "Bottle" :price 9.90  :in? true  :tags ["health"]}])

(def total
  (reduce + 0 (map :price products)))

(defn page
  "Return hiccup tree for the products page."
  []
  (h/html5 {:lang "en"}
    (h/head
      (h/meta-charset "utf-8")
      (h/title* "CljPages — Products"))
    (h/body
      (h/header
        (h/h1 "CljPages demo site")
        (h/nav
          (h/a "/index.html" "Home") " | "
          (h/a "/users/index.html" "Users") " | "
          (h/a "/products/index.html" "Products") " | "
          (h/a "/blog/2025/post-1.html" "Blog 2025/1")))
      (h/section
        (h/div {:class "card"}
          (h/h2 "Products table")
          (h/p "Total value: " (format "%.2f" (double total)))
          (h/table
            (h/thead
              (h/tr
                (h/th "Name")
                (h/th "Price")
                (h/th "In stock?")
                (h/th "Tags")))
            (h/tbody
              (for [p products]
                (h/tr
                  (h/td (:name p))
                  (h/td (:price p))
                  (h/td (if (:in? p) "Yes" "No"))
                  (h/td
                    (for [t (:tags p)]
                      (h/span {:class "tag"} t)))))))))
      (h/footer
        (h/small "Generated with CljPages — tag-only core")))))
