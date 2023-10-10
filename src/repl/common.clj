(ns repl.common
  "Common functions to enhance REPL-driven development."
  (:require
    [clojure.java.process :as process]
    [clojure.pprint :as pp]))

(defn ? [x] (doto x pp/pprint))

(defn sh
  "Shell out and provide real-time stdout & stderr feedback."
  [cmd & args]
  (apply process/start {:out :inherit :err :inherit} cmd args))

(defn bash
  "Execute a shell command with bash."
  [cmdline-str]
  (sh "bash" "-c" cmdline-str))

(defn nix-run
  "Execute a shell command within a nix-shell."
  [cmdline-str]
  (sh "nix-shell" "-p" "awscli2" "--run" cmdline-str))
