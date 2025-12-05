(ns cljpages.core
  "CljPages — tag-only HTML builders for Clojure.
   This namespace intentionally contains only functions:
   - hiccup-like HTML tag helpers
   - a tiny renderer (hiccup -> HTML string)
   - `emit!` and `emit-batch!` to write HTML to disk"
  (:require [clojure.java.io :as io]))

;; ------------------------------------------------------------
;; Rendering (hiccup data structure -> HTML string)
;; ------------------------------------------------------------

(defn ^String html-escape
  "HTML-escape an arbitrary value.
   - nil           -> \"\"
   - everything    -> coerced to string, then &, <, >, \" and ' are escaped."
  ^String [^Object s]
  (let [s (str (or s ""))]
    (-> s
        (.replace "&" "&amp;")
        (.replace "<" "&lt;")
        (.replace ">" "&gt;")
        (.replace "\"" "&quot;")
        (.replace "'" "&#39;"))))

(declare render-node)

(defn render-attrs
  "Render a Clojure map of attributes to a string suitable for use in a tag.
   Input:
     {:class \"btn\" :data-id 42}
   Output:
     \" class=\\\"btn\\\" data-id=\\\"42\\\"\"  (leading space included)."
  [m]
  (if (and m (seq m))
    (apply str
           (for [[k v] m
                 :when (some? v)]
             (str " " (name k) "=\"" (html-escape v) "\"")))
    ""))

(def void-tags
  "Set of HTML tags that are void (no closing tag, no children)."
  #{:area :base :br :col :embed :hr :img :input :link :meta
    :param :source :track :wbr})

(defn render-tag
  "Render a single hiccup vector representing one HTML element.
   Supported shapes:
     [tag]
     [tag child1 child2 ...]
     [tag {:attr \"v\"} child1 child2 ...]"
  [[tag maybe-attrs & children]]
  (let [[attrs real-children]
        (if (map? maybe-attrs)
          [maybe-attrs children]
          [nil (cons maybe-attrs children)])]
    (if (void-tags tag)
      ;; Void tag like <br /> or <img ... />
      (str "<" (name tag) (render-attrs attrs) " />")
      ;; Normal tag with children
      (str "<" (name tag) (render-attrs attrs) ">"
           (apply str (map render-node real-children))
           "</" (name tag) ">"))))

(defn render-node
  "Render any supported node:
   - nil          -> \"\"
   - string/char  -> HTML-escaped
   - number       -> plain string
   - keyword      -> its name (no colon)
   - map          -> HTML-escaped `str` representation
   - vector       -> treated as hiccup tag, passed to `render-tag`
   - seq          -> each element rendered and concatenated
   - everything else -> HTML-escaped `str`."
  [node]
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

;; ------------------------------------------------------------
;; Tag helpers (all return hiccup vectors)
;; ------------------------------------------------------------

(defn doctype5
  "Return the HTML5 doctype declaration."
  []
  "<!doctype html>")

(defn html5
  "Convenience wrapper that renders a complete HTML5 document.
   Usage:
     (html5 {:lang \"en\"}
       (head (meta-charset \"utf-8\")
             (title* \"Demo\"))
       (body (h1 \"Hi\")))"
  [attrs & children]
  (str (doctype5)
       (render-tag (into [:html attrs] children))))

(defn head [& xs] (into [:head] xs))
(defn body [& xs] (into [:body] xs))
(defn title* [s] [:title (str s)])
(defn meta-charset [enc] [:meta {:charset enc}])
(defn link-css [href] [:link {:rel "stylesheet" :href href}])
(defn script-src [src] [:script {:src src}])
(defn style [css] [:style css])

(defn div
  "Div helper that accepts:
   - no args:          (div)                      -> [:div]
   - children only:    (div child1 child2 ...)    -> [:div child1 child2 ...]
   - attrs + children: (div {:class \"x\"} ...)   -> [:div {:class \"x\"} ...]"
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

;; ------------------------------------------------------------
;; Emit to disk
;; ------------------------------------------------------------

(defn emit!
  "Render hiccup-or-string and write it to `path`.
   - `path` can be a string or java.io.File
   - creates parent directories if necessary
   Returns the absolute path as a string."
  [path hiccup-or-string]
  (let [file (io/file path)
        s    (if (string? hiccup-or-string)
               hiccup-or-string
               (render-node hiccup-or-string))]
    (io/make-parents file)
    (spit file s)
    (.getPath (.getAbsoluteFile file))))

(defn- pages->pairs
  "Normalize `pages` into a sequence of [relative-path hiccup-or-string] pairs.
   Supported shapes:

   1) Flat map with string keys:
      {\"index.html\" page
       \"blog/post.html\" page}

   2) Nested tree (directories as maps):
      {\"index.html\" home-page
       \"blog\" {\"index.html\" blog-index
                \"2025\" {\"post-1.html\" post-1}}}

      Keys may be strings or keywords. Keywords are turned into their `name`.

   3) Any seq of [rel-path page]."
  [pages]
  (letfn [(walk [prefix m]
            (mapcat
              (fn [[k v]]
                (let [k-str (if (keyword? k) (name k) (str k))]
                  (if (map? v)
                    ;; Directory node
                    (walk (str prefix k-str "/") v)
                    ;; Leaf node
                    [[(str prefix k-str) v]])))
              m))]
    (cond
      ;; Nested or flat map
      (map? pages)
      (let [string-key-map? (every? string? (keys pages))]
        (if string-key-map?
          ;; Already a flat map with string keys, keep as-is
          (seq pages)
          ;; Treat as nested tree
          (walk "" pages)))

      ;; Already a seq of pairs
      :else pages)))

(defn emit-batch!
  "Emit many HTML files under a base directory.

   `base-dir` — root output folder (string or java.io.File).
   `pages`    — one of:

     1) Flat map:
        {\"index.html\"        page
         \"blog/post-1.html\"  page}

     2) Nested tree (recommended for larger sites):
        {\"index.html\" home-page
         \"blog\" {\"index.html\" blog-index
                  \"2025\" {\"post-1.html\" post-1}}}

        Keys may be strings or keywords. Directories are represented by maps.

     3) Seq of pairs:
        [[\"index.html\"       page]
         [\"blog/post-1.html\" page]]

   All relative paths are resolved under `base-dir`.

   Returns the absolute base directory path as a string."
  [base-dir pages]
  (let [root  (io/file base-dir)
        pairs (pages->pairs pages)]
    (doseq [[rel-path hiccup-or-string] pairs]
      (let [target (io/file root (str rel-path))]
        (emit! target hiccup-or-string)))
    (.getPath (.getAbsoluteFile root))))
