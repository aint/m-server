#!/bin/sh
ssh -D 9696 adminl@192.168.11.84

ssh -f -N adminl@192.168.11.84 -L 4000:127.0.0.1:4000