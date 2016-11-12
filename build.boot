(set-env!
  :source-paths #{"src/clj"}
  :dependencies '[[me.raynes/conch "0.8.0"]])

(def version "0.1.1")


(deftask build
  "Build my project."
  []
  (comp
     (aot 
         :namespace ['sudoku.core])
     (pom
         :project 'com.puffingtonpress/sudoku
         :version version) 
     (uber)
     (jar
         :main 'sudoku.core
         :file (str "sudoku-" version ".jar"))
     (target 
         :dir #{"target"})))
