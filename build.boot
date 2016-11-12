(set-env!
  :resource-paths #{"src/clj"}
  :dependencies '[[me.raynes/conch "0.8.0"]])

(deftask build
  "Build my project."
  []
  (comp
     (aot 
         :namespace ['sudoku.core])
     (pom
         :project 'sudoku
         :version "0.1.0") 
     (jar
         :main 'sudoku.core
         :manifest {"Foo" "bar"})
     (install)))
