import NGSIClient

l = [1,2,3]
print "Puttin context ..."
NGSIClient.createContext("Owon", "CubaLibre", NGSIClient.createMeasureArray("centrig", "temp", l))
print "Getting context ..."
NGSIClient.getContext("Owon", "CubaLibre")
print "Finished"
