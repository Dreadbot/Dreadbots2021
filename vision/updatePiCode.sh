#!/bin/bash
echo "Must know pi's password!!!"
echo "If you do not, contact cole"
while test $# -gt 0; do
  case "$1" in
    -h)
      shift
      echo "-p : Pull the code only (don't run)"
      echo "-r : Run the code without pulling"
  esac
  case "$1" in
    -r)
      shift
      scp vision2021.py #dest-ip
      ssh pi@ #dest- ip # python3 vision2021.py
  esac
  case "$1" in
    -p)
      shift
      git pull
      scp vision2021.py #dest-ip
  esac
done
