import numpy as np
import cv2

cascade = cv2.CascadeClassifier('cascade.xml')

cap = cv2.VideoCapture(0)

while True:
	ret, img = cap.read()

	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	results = cascade.detectMultiScale(gray, 50, 50)
	for (x,y,w,h) in results:
		cv2.rectangle(img, (x,y), (x+w, x+j), (255,0,0), 2)

	cv2.imshow('img', img)
	k = cv2.waitKey(30) & 0xff
	if k == 27:
		break

cap.release()
cv2.destroyAllWindows()
