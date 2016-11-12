(ns sudoku.core
  (:require 
    [clojure.string :as str]
    [clojure.data :as data])
  (:gen-class))

(def MAX-INDEX 81)
(def MAX-NUM 9)
(def SQ-NUM 3)
(def NUM-SET #{1 2 3 4 5 6 7 8 9})

(defn parse-file [filename]
  (let [lines (str/split-lines (slurp filename))]
    (loop [lines lines
           grids []
           data ""]
         (if lines
           (let [line (first lines)]
             (println line)
             (if (= (get line 0) \G)
               (if (= 0 (count data))
                 (recur (next lines) grids data)
                 (recur (next lines) (conj grids (into [] (map (fn [^Character c] (Character/digit c 10)) data))) ""))
               (recur (next lines) grids (str data line))))
           (conj grids (into [] (map (fn [^Character c] (Character/digit c 10)) data)))))))

(defn get-col-vals [cells col-starting-index]
  (loop [num 0
         col-val []]
        (if (> MAX-NUM num)
          (recur (+ num 1) (conj col-val (nth cells (+ col-starting-index (* num MAX-NUM)))))
          col-val)))

(defn get-sq-vals [cells sq-starting-index]
  (loop [num 0
         sq-val []]
        (if (> SQ-NUM num)
          (recur (+ num 1) (conj sq-val (nth cells (+ sq-starting-index (* num MAX-NUM))) (nth cells (+ sq-starting-index (* num MAX-NUM) 1)) (nth cells (+ sq-starting-index (* num MAX-NUM) 2))))
          sq-val)))

(defn get-valid-nums [cells index]
  (let [row-starting-index (* (quot index MAX-NUM) MAX-NUM)
        col-starting-index (mod index MAX-NUM)
        square-starting-index (+ (* (quot index (* MAX-NUM SQ-NUM)) (* MAX-NUM SQ-NUM)) (* (quot (mod index MAX-NUM) SQ-NUM) SQ-NUM))
        row-vals (subvec cells row-starting-index (+ row-starting-index MAX-NUM))
        col-vals (get-col-vals cells col-starting-index)
        sq-vals (get-sq-vals cells square-starting-index)]

       (data/diff NUM-SET (into #{} (into (into row-vals col-vals) sq-vals)))))


(defn valid-cell [cells index num]
  (let [row-starting-index (* (quot index MAX-NUM) MAX-NUM)
        col-starting-index (mod index MAX-NUM)
        square-starting-index (+ (* (quot index (* MAX-NUM SQ-NUM)) (* MAX-NUM SQ-NUM)) (* (quot (mod index MAX-NUM) SQ-NUM) SQ-NUM))
        row-vals (subvec cells row-starting-index (+ row-starting-index MAX-NUM))
        col-vals (get-col-vals cells col-starting-index)
        sq-vals (get-sq-vals cells square-starting-index)]
       (if (contains? (into #{} (into (into row-vals col-vals) sq-vals)) num)
         false
         true)))

(defn solve-next-cell [cells index]
  (if (> MAX-INDEX index)
    ;; do we need to solve or already something here?
    (if (= (nth cells index) 0)
      ;;need to solve - get valid numbers
      (let [valid-nums (first (get-valid-nums cells index))]
           (loop [nums valid-nums]
;;               (println (str index "-" nums))
                 (if nums
                   (let [num (first nums)
                         solution (solve-next-cell (assoc cells index num) (+ index 1))]
                        (if (empty? solution)
                          (recur (next nums))
                          solution))
                   [])))
      (solve-next-cell cells (+ index 1)))
    cells))

(defn solve-next-cell-brute [cells index]
  (if (> MAX-INDEX index)
    ;; do we need to solve or already something here?
    (if (= (nth cells index) 0)
      ;;need to solve
      (loop [num 1]
;;         (println (str index "-" num))
         (if (>= MAX-NUM num)
            (if (valid-cell cells index num)
              (let [solution (solve-next-cell (assoc cells index num) (+ index 1))]
                   (if (empty? solution)
                     (recur (+ num 1))
                     solution))
              (recur (+ num 1)))
            []))
      (solve-next-cell cells (+ index 1)))
    cells))
 
(defn solve-grid [grid]
       (let [solution (solve-next-cell grid 0)]
           solution))

(defn build-number [huns tens ones]
  (+ (* huns 100) (* tens 10) ones))


(defn sum-sols [sols]
  (println sols)
  (loop [sols sols
          sum-sol 0]
        (if sols
          (let [sol (first sols)
               sol-num (build-number (nth sol 0) (nth sol 1) (nth sol 2))] 
               (recur (next sols) (+ sum-sol sol-num)))
          (println sum-sol))))

              
(defn -main
  "Given a file of soduko starting puzzles solve them and display the sum of the first 3 digits"
  [& args]
  (let [grids (parse-file (first args))]
       (println grids) 
       (loop [grids grids
              sols []]
          (if grids
            (let [grid (first grids)]
                 (recur (next grids) (conj sols (solve-grid grid))))
            (sum-sols sols)))))

;;(defn read-file [filename]
;;  (with-open [rdr (io/reader filename)]

;;    (doseq [line (line-seq rdr)]
      ;; need to split grids up
;;      (if (= (get line 0) \G)
        ;; start new grid
        
;;        (println (format "new %s" line)) 
;;        (println line)))))


;;(parse-file "p096_sudoku.txt")
;;(solve-grid "003020600900305001001806400008102900700000008006708200002609500800203009005010300")
;;(valid-cell [0 0 3 0 2 0 6 0 0 9 0 0 3 0 5 0 0 1 0 0 1 8 0 6 4 0 0 0 0 8 1 0 2 9 0 0 7 0 0 0 0 0 0 0 8 0 0 6 7 0 8 2 0 0 0 0 2 6 0 9 5 0 0 8 0 0 2 0 3 0 0 9 0 0 5 0 1 0 3 0 0] 5 1) 

;; 483 245 462

