import json

#All calis must be lowercase
def readCalibration(setting=None):
    #Checking if the call used the setting flag
    if setting is not None:
        #If they did, make the string lowercase as thats the convention
        setting.lower()
    else:
        setting = "default"

    #Open the calibrations file
    with open("calibrations.json", "r") as f:
        #Read the file
        dump = f.read()
        calibrations = json.loads(dump)
        f.close()
    
    return(calibrations[setting])

def addCalibration(settingName, hue, sat, lum):
    #Open the calibrations file
    with open("calibrations.json", "r") as f:
        #Read the file
        dump = f.read()
        calibrations = json.loads(dump)
        f.close()
    
    #Create the dictionary to add to the calibrations
    calibration = {
        settingName : {
            "hue" : hue,
            "sat" : sat,
            "lum" : lum
        }
    }

    #Add this calibration on top of the prev ones
    calibrations.update(calibration)

    #Write over the calibration file with the new calibration file
    with open("calibrations.json", "w+") as f:
        json.dump(calibrations, f, indent=4)
