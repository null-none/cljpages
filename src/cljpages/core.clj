(ns cljpages.core
  "CljPages — tag-only HTML builders for Clojure.
   This namespace intentionally contains **only functions**:
   - hiccup-like HTML tag helpers
   - a tiny renderer
   - `emit!` to write HTML to a file"
  (:require [clojure.java.io :as io]))

;; -------- Rendering (hiccup -> HTML string) --------

(defn ^String html-escape [^Object s]
  (let [s (str (or s ""))]
    (-> s
        (.replace "&" "&amp;")
        (.replace "<" "&lt;")
        (.replace ">" "&gt;")
        (.replace "\"" "&quot;")
        (.replace "'" "&#39;"))))

(declare render-node)

(defn render-attrs [m]
  (if (and m (seq m))
    (apply str
           (for [[k v] m :when (some? v)]
             (str " " (name k) "=\"" (html-escape v) "\"")))
    ""))

(def void-tags #{:area :base :br :col :embed :hr :img :input :link :meta :param :source :track :wbr})

(defn render-tag [[tag maybe-attrs & children]]
  (let [[attrs real-children]
        (if (map? maybe-attrs)
          [maybe-attrs children]
          [nil (cons maybe-attrs children)])]
    (if (void-tags tag)
      (str "<" (name tag) (render-attrs attrs) " />")
      (str "<" (name tag) (render-attrs attrs) ">"
           (apply str (map render-node real-children))
           "</" (name tag) ">"))))

(defn render-node [node]
  (cond
    (nil? node) ""
    (string? node) (html-escape node)
    (char? node) (html-escape (str node))
    (number? node) (str node)
    (keyword? node) (name node)
    (map? node) (html-escape (str node))
    (vector? node) (render-tag node)
    (sequential? node) (apply str (map render-node node))
    :else (html-escape (str node))))

;; -------- Tag helpers (all return vectors) --------

(defn doctype5 [] "<!doctype html>")
(defn html5 [attrs & children] (str (doctype5) (render-tag (into [:html attrs] children))))
(defn head [& xs] (into [:head] xs))
(defn body [& xs] (into [:body] xs))
(defn title* [s] [:title (str s)])
(defn meta-charset [enc] [:meta {:charset enc}])
(defn link-css [href] [:link {:rel "stylesheet" :href href}])
(defn script-src [src] [:script {:src src}])
(defn style [css] [:style css])

(defn div
  "Div helper that accepts:
   - no args:              (div) -> [:div]
   - children only:        (div child1 child2 ...)
   - attrs + children:     (div {:class \"x\"} child1 child2 ...)"
  ([] [:div])
  ([x & more]
   (if (map? x)
     (into [:div x] more)
     (into [:div] (cons x more)))))
(defn span [& xs] (into [:span] xs))
(defn p [& xs] (into [:p] xs))
(defn h1 [& xs] (into [:h1] xs))
(defn h2 [& xs] (into [:h2] xs))
(defn h3 [& xs] (into [:h3] xs))
(defn h4 [& xs] (into [:h4] xs))
(defn h5 [& xs] (into [:h5] xs))
(defn h6 [& xs] (into [:h6] xs))
(defn ul [& xs] (into [:ul] xs))
(defn ol [& xs] (into [:ol] xs))
(defn li [& xs] (into [:li] xs))
(defn a [href text] [:a {:href href} (str text)])
(defn img [attrs] [:img attrs])
(defn br [] [:br])
(defn hr [] [:hr])

(defn table [& xs] (into [:table] xs))
(defn thead [& xs] (into [:thead] xs))
(defn tbody [& xs] (into [:tbody] xs))
(defn tr [& xs] (into [:tr] xs))
(defn th [& xs] (into [:th] xs))
(defn td [& xs] (into [:td] xs))

(defn form [attrs & xs] (into [:form attrs] xs))
(defn input [attrs] [:input attrs])
(defn label [attrs & xs] (into [:label attrs] xs))
(defn button [attrs & xs] (into [:button attrs] xs))

(defn header [& xs] (into [:header] xs))
(defn footer [& xs] (into [:footer] xs))
(defn section [& xs] (into [:section] xs))
(defn article [& xs] (into [:article] xs))
(defn nav [& xs] (into [:nav] xs))
(defn main [& xs] (into [:main] xs))
(defn small [& xs] (into [:small] xs))
(defn strong [& xs] (into [:strong] xs))
(defn em [& xs] (into [:em] xs))
(defn code [& xs] (into [:code] xs))
(defn pre [& xs] (into [:pre] xs))

;; -------- Emit to file --------

(defn emit! [path hiccup-or-string]
  (let [s (if (string? hiccup-or-string) hiccup-or-string (render-node hiccup-or-string))]
    (io/make-parents path)
    (spit path s)
    path))