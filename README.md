# CljPages

Lightweight HTML builder in pure Clojure, using only simple tag functions and a renderer.

- `src/cljpages/core.clj` — only tag helpers + renderer + `emit!` (no example, no `-main`)
- `example/example.clj` — sample usage that builds a page and writes `out/index.html`

## Run the example
```bash
clj -M:example
# OK → out/index.html
# open out/index.html
```

## Use in a REPL
```clojure
(require '[cljpages.core :as h])

(def page
  (h/html5 {:lang "en"}
    (h/head (h/meta-charset "utf-8") (h/title* "Hello"))
    (h/body (h/h1 "Hi") (h/p "This is a paragraph.") (h/a "/about" "About us"))))

(h/emit! "out/hello.html" page)
```