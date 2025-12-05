(ns pages.users
  "Users page definition."
  (:require [cljpages.core :as h]))

(def users
  [{:name "Ann"   :age 22 :vip true}
   {:name "Boris" :age 31 :vip false}
   {:name "Chen"  :age 28 :vip true}])

(defn page
  "Return hiccup tree for the users page."
  []
  (h/html5 {:lang "en"}
    (h/head
      (h/meta-charset "utf-8")
      (h/title* "CljPages — Users"))
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
          (h/h2 "Users list")
          (h/ul
            (for [u users]
              (h/li
                (h/strong (:name u))
                " — "
                (:age u)
                " years old"
                (when (:vip u)
                  (h/span {:class "vip"} " VIP")))))))
      (h/footer
        (h/small "Generated with CljPages — tag-only core")))))
