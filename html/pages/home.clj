(ns pages.home
  "Home page definition."
  (:require [cljpages.core :as h]))

(defn page
  "Return hiccup tree for the home page."
  []
  (h/html5 {:lang "en"}
    (h/head
      (h/meta-charset "utf-8")
      (h/title* "CljPages — Home"))
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
          (h/h2 "Welcome")
          (h/p "This is a tiny static site generated from plain Clojure data.")
          (h/p "Pages are rendered using simple tag functions and written with "
               (h/code "emit-batch!")
               ".")))
      (h/footer
        (h/small "Generated with CljPages — tag-only core")))))
