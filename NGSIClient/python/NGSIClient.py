import requests
import json

def createContext(type, id, attributes):
  body =  {
        "contextElements" : {
                "contextElement": {
                        "type": type,
                        "isPattern": "false",
                        "id": id,
			"attributes": attributes
                }
        },
	"updateAction": "APPEND"
  }
  headers = {
        "Accept": "application/json",
        "Content-type": "application/json"
  }
  r = requests.post("http://130.206.85.233:1026/NGSI10/updateContext", data=json.dumps(body), headers=headers)
  print "The response code is " + str(r.status_code)

  if r.status_code == 200:
    print "Message sent succesfully"

  elif r.status_code == 405:
    print "There was an error"

  else:
    print "Unknown result"

#put:
#	(curl $(DEST):1026/ngsi10/contextEntities/prueba -X POST -s -S --header 'Content-Type: application/json' --header 'Accept: application/json' \
#       --header "X-Auth-Token: $(AUTH_TOKEN)" -d @- | python -mjson.tool) < prueba>
def createContext2():
  body = {
          "attributes" : [
    {
      "name" : "city_location",
      "type" : "city",
      "value" : "Madrid"
    },
    {
      "name" : "temperature",
      "type" : "float",
      "value" : "25.8"
    }
  ]
  }
  headers = {
        "Accept": "application/json",
        "Content-type": "application/json"
  }
  r = requests.post("http://130.206.85.233:1026/NGSI10/contextEntities/prueba", data=json.dumps(body), headers=headers)
  print "The response code is " + str(r.status_code)

  if r.status_code == 200:
    print "Message sent succesfully"

  elif r.status_code == 405:
    print "There was an error"

  else:
    print "Unknown result"

def getContext(type, id):
  body =  { 
	"entities" : [ 
		{
  			"type": type,
			"isPattern": "false",
			"id": id
		}
	] 
  }
  headers = {
	"Accept": "application/json",
	"Content-type": "application/json"
  }
  r = requests.post("http://130.206.85.233:1026/NGSI10/queryContext", data=json.dumps(body), headers=headers)
  print "The response code is " + str(r.status_code)

  if r.status_code == 200:
    print "The contest is: " + r.content
    print "The body is: " + r.text

  elif r.status_code == 405:
    print "There was an error"

  else:
    print "Unknown result"

#get2:
#	curl $(DEST):1026/ngsi10/contextEntities/prueba -X GET -s -S --header 'Content-Type: application/json' \
#      --header 'Accept: application/json' --header "X-Auth-Token: $(AUTH_TOKEN)" | python -mjson.tool
def getContext2():
  #body =  {}
  headers = {
	"Accept": "application/json",
	"Content-type": "application/json"
  }
  r = requests.get("http://130.206.85.233:1026/NGSI10/contextEntities/prueba", headers=headers)
  print "The response code is " + str(r.status_code)

  if r.status_code == 200:
    print "The content is: " + r.content

  elif r.status_code == 405:
    print "There was an error"

  else:
    print "Unknown result"

def createMeasureArray(measureType, measureName, arrayValues):
  values = []
  for i in range(len(arrayValues)):
    valueJson = {
	"name": measureName + str(i),
	"type": measureType,
	"value": arrayValues[i]
    }
    values.append(valueJson)

  return values

