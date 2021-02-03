#!/usr/bin/env python

import json
import sys
import getopt
import numpy as np
import cv2
import threading

help_msg = '''
Python vision 2021

Commands:
-h - Display this message
-s - set the setting (-s <setting name>)
-d - Debug setting

If you need help contact a vision member
'''


# All calibrations must be lowercase
def read_calibration(setting):
    # Open the calibrations file
    with open("calibrations.json", "r") as f:
        # Read the file
        dump = f.read()
        calibrations = json.loads(dump)
        f.close()

    return calibrations[setting]


def add_calibration(setting_name, hue, sat, lum):
    # Open the calibrations file
    with open("calibrations.json", "r") as f:
        # Read the file
        dump = f.read()
        calibrations = json.loads(dump)
        f.close()

    # Create the dictionary to add to the calibrations
    calibration = {
        setting_name: {
            "hue": hue,
            "sat": sat,
            "lum": lum
        }
    }

    # Add this calibration on top of the prev ones
    calibrations.update(calibration)

    # Write over the calibration file with the new calibration file
    with open("calibrations.json", "w+") as f:
        json.dump(calibrations, f, indent=4)


def pipe_image(img, hue, lum, sat, dil_iterations=20):
    hls_img = cv2.cvtColor(img, cv2.COLOR_BGR2HLS)
    bw_img = cv2.inRange(hls_img, (hue[0], lum[0], sat[0]), (hue[1], lum[1], sat[1]))

    # Dilate areas of high val then close holes
    img_dilate = cv2.dilate(bw_img, None, dil_iterations)
    img_closing = cv2.morphologyEx(img_dilate, cv2.MORPH_CLOSE, None)
    return bw_img


def img_thread(setting="default", debug_mode=False):
    if debug_mode is not True:
        calibration = read_calibration(setting)

        hue = calibration['hue']
        sat = calibration['sat']
        lum = calibration['lum']
    else:
        hue = [None, None]
        sat = [None, None]
        lum = [None, None]
        hue[0] = int(input("Hue lower: "))
        hue[1] = int(input("Hue upper: "))
        sat[0] = int(input("Sat lower: "))
        sat[1] = int(input("Sat upper: "))
        lum[0] = int(input("Lum lower: "))
        lum[1] = int(input("Lum upper: "))

    print(hue)
    print(lum)
    print(sat)
    driveCap = cv2.VideoCapture(2)
    driveCap.set(cv2.CAP_PROP_AUTO_EXPOSURE, 1)  # For some reason 1 works, idk man
    driveCap.set(cv2.CAP_PROP_EXPOSURE, .00001)

    while True:
        ret, frame = driveCap.read()
        out_img = pipe_image(frame, hue, sat, lum)
        cv2.imshow('frame', out_img)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    driveCap.release()
    cv2.destroyAllWindows()


def main(argv):
    setting = ''
    try:
        opts, args = getopt.getopt(argv,"s:d",["setting="])
    except getopt.GetoptError:
        print(help_msg)
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print(help_msg)
            sys.exit()
        elif opt in ("-s", "--setting"):
            setting = arg
            print(read_calibration(setting))
        elif opt == '-d':
            img_thread(debug_mode=True)


if __name__ == "__main__":
    main(sys.argv[1:])

# TODO
# - Simplify debug/self-calibrate mode
# - Lay out power cell detection
