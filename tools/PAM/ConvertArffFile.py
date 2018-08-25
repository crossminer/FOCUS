import os

lpath = "C://Users//claudio//Desktop//focusToPam//"
lpath2 = "C://Users//claudio//Desktop//dataset//"

c=0
#per claudio, prende i file con il formato arff errato e li rigenera correttamente

for f in os.listdir(lpath):
    my_dict = {}
    with open(os.path.join(lpath, f),"r",encoding='utf-8', errors='ignore') as file:
        count = 0
        for line in file:
            if count > 2:
                partLine = line.split("','")
                md = partLine[0]
                mi = partLine[1]
                md = md[2:]
                mi = mi[:-2]
                if md in my_dict:
                    l = my_dict[md]
                    l.append(mi)
                else:
                    my_dict[md] = [mi]
            count = count +1
    result = ""
    for key, value in my_dict.items():
        result = result + "'" + key + "','"
        for element in value:
            result = result + element + " "
        result = result[:-2] + "'\n"
    #with open(os.path.join(lpath2, f), "w") as file:
    i = f.find(".txt")
    f = f[:i]
    with(open(lpath2+f+".arff","w",encoding='utf-8', errors='ignore')) as file:
        file.write(result)





