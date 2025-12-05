(ns pages.blog
  "Blog pages definitions."
  (:require [cljpages.core :as h]))

(defn post-1
  "Return hiccup tree for blog post 1."
  []
  (h/html5 {:lang "en"}
    (h/head
      (h/meta-charset "utf-8")
      (h/title* "CljPages — Blog 2025/1"))
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
          (h/h2 "Nested directories demo")
          (h/p "This page lives at "
               (h/code "blog/2025/post-1.html")
               " and is generated via a nested map passed to "
               (h/code "emit-batch!")
               ".")
          (h/p "Directories are just nested maps in the `pages` structure.")))
      (h/footer
        (h/small "Generated with CljPages — tag-only core")))))
