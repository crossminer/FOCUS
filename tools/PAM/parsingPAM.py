import os
import shutil
from distutils.dir_util import copy_tree
def splitFiles():
    PAM_path="C://Users//claudio//Desktop//PAM_all//"
    total_row=0
    for f in os.listdir(PAM_path):
        print(f)
        file = open(PAM_path+f,"r",encoding='utf-8', errors='ignore')

        lines=file.readlines()
        tot=len(lines)
        rmv=(tot*2)/3
        print(int(rmv))
        part = open("C://Users//claudio//Desktop//folder1//temp//" + f, "w",encoding='utf-8', errors='ignore')
        part2= open("C://Users//claudio//Desktop//PAM_processed//Round1//removed//" + f, "w",encoding='utf-8', errors='ignore')
        i=0
        c=0
        #part.write("@relation project\n @attribute fqCaller string \n  @attribute fqCalls string \n @data \n")
        for line in lines:
            part.write(line)
            i+=1
            if(i>=int(rmv)):
                break
        i=rmv
        for line2 in lines:

            i+=1
            c+=1

            print(c)
            if(c>=int(rmv)):
                part2.write(line2)
                if(c>=tot):
                    break

def getMethodInv():
    n=0
    i=0
    path="C://Users//claudio//Desktop//PAM_processed//Round1//baseline//"
    pathResults="C://Users//claudio//Desktop//PAM_processed//Round1//TestingInvocations//"
    #temp=open("temp.txt","w")

    list_inv=[]
    for f in os.listdir(path):
        file = open(path + f, "r",encoding='utf-8', errors='ignore')
        temp=open(pathResults+f,"w",encoding='utf-8', errors='ignore')
        for line in file.readlines():
            methodcalls=line[line.find("','")+3:]

            print(methodcalls)
            n=methodcalls.count(" ")
            if(n>=4):
                i=i+1
                if (i>3):
                    inv=methodcalls.replace(" ","\n")
                    print(line[:line.find("','")+3]+inv)
                    temp.write(inv)
                    break


def groundT(path="C://Users//claudio//Desktop//folder1//TestingInvocations//"):
    pathResults = "C://Users//claudio//Desktop//folder1//GroundTruth//"
    gtfile = open("gtfile.txt", "w")
    calls=open("temp.txt","r")
    i=0
    for f in os.listdir(path):
        calls = open(path + f, "r")
        gtfile=open(pathResults+f,"w")
        for lines in calls:

            i = i + 1
            if (i > 2):
                break


def combineMethodsInv():
    root_processed="C://Users//claudio//Desktop//folder1//temp//"
    root_testing="C://Users//claudio//Desktop//folder1//TestingInvocations//"
    root_arff="C://Users//claudio//Desktop//folder1//Arff//"

    for f in os.listdir(root_processed):
        file=open(root_processed+f,"r",encoding='utf-8', errors='ignore')
        thisFile = os.path.basename(file.name)
        i = thisFile.find(".arff")
        thisFile = thisFile[:i]
        file2=open(root_testing+thisFile+".txt","r",encoding='utf-8', errors='ignore')
        thisFile = os.path.basename(file2.name)
        j = thisFile.find(".txt")
        thisFile = thisFile[:j]
        result_arff = open(root_arff+thisFile+".arff","w", encoding='utf-8', errors='ignore')
        result_arff.write("@relation project\n @attribute fqCaller string \n  @attribute fqCalls string \n @data \n")
        for e in file:
            result_arff.write(e)
        for e2 in file2:
            result_arff.write(e2)


def combineAll(filename):
    start=open("C://Users//claudio//Desktop//folder1//Arff//"+filename,"r",encoding="utf-8",errors="ignore")
    resultArff=open("C://Users//claudio//Desktop//folder1//PAM_arff//"+filename,"w",encoding="utf-8",errors="ignore")
    for e in start:
        resultArff.write(e)
    folders="C://Users//claudio//Desktop//PAM_all//"
    for e in os.listdir(folders):
        file=open(folders+e,"r",encoding="utf-8",errors="ignore")
        for row in file:
            resultArff.write(row)


def renamingFile():
    root_testing = "C://Users//claudio//Desktop//folder1//TestingInvocations//"
    root_arff="C://Users//claudio//Desktop//folder1//ex"
    for f in os.listdir(root_testing):
        file=open(root_testing+f,"r",encoding="utf-8",errors="ignore")
        thisFile=os.path.basename(file.name)
        i=thisFile.find(".txt")
        thisFile=thisFile[:i]
        print(thisFile)
        file = open(root_arff + thisFile+".arff", "w", encoding="utf-8", errors="ignore")




def findMalformed():
    lpath = "C://Users//claudio//Desktop//Focus//Focus//"
    lpath2 = "C://Users//claudio//Desktop//PAM_results//"
    moveTo="C://Users//claudio//Desktop//wrongFormat//"
    pam=open("pam.txt","w")
    wrong = open("wrong.txt", "w")
    errors = open("focus.txt", "w")

    for f in os.listdir(lpath):
        pamFile=open(lpath+f,"r")
        name=os.path.basename(pamFile.name)
        i=name.find(".")
        name=name[:i]
        pam.write(name+"\n")


    for f2 in os.listdir(lpath2):
        file=open(lpath2+f2)
        name2=os.path.basename(file.name)
        j=name2.find(".")
        name2=name2[:j]

        wrong.write(name2+"\n")
    list1=[]
    list2=[]
    pam = open("pam.txt", "r")
    #wrong = open("wrong.txt", "r")
    lines=pam.readlines()
    for l in lines:
        list1.append(l)

    lines2=wrong.readlines()

    for l2 in lines2:
        list2.append(l2)
        print(l2)

    res=[x for x in list1 if x not in list2]
    for e in res:
        errors.write(e)

def retreiveList():
    dir="C://Users//claudio//Desktop//left//"
    err1 = open("err1.txt", "w")
    err2 = open("err2.txt", "w")
    final = open("final.txt", "w")
    l1=[]
    l2=[]
    listPhoung = "C://Users//claudio//Desktop//Focus//Focus//"
    for d in os.listdir(dir):
        d = open(dir + d)
        name2 = os.path.basename(d.name)
        j = name2.find(".")
        d = name2[:j]
        l1.append(d)
        #114

    #lines=listPhoung.readlines()
    for l in os.listdir(listPhoung):
        l = open(listPhoung + l)
        name = os.path.basename(l.name)
        i = name.find(".")
        l = name[:i]
        l2.append(l)
        #548
    res1 = [x for x in l1 if x not in l2]
    for e in res1:
        err1.write(e+"\n")
    res2 = [x for x in l2 if x not in l1]
    for e1 in res2:
        err2.write(e1 + "\n")
    err1 = open("err1.txt", "r")
    err2 = open("err2.txt", "r")
    lines=err1.readlines()
    lines2 = err2.readlines()
    listF=[x for x in lines2 if x not in lines]
    for f in listF:
        final.write(f)

def movingFiles():
    fileSource = open("C://Users//claudio//Desktop//Evaluation//Evaluation//List.txt", "r")
    lines=fileSource.readlines()
    listsrc=open("ciao.txt","w")

    for l in lines:
        j = l.find(".")
        d = l[:j]
        listsrc.write(d+"\n")


    listsrc = open("ciao.txt", "r")

    src="C://Users//claudio//Desktop//Focus//Focus//"
    dst="C://Users//claudio//Desktop//PAM_dataset//"
    print("dsds")
    for s in listsrc:

        try:
            fromDirectory = src+ s.rstrip()
            toDirectory = dst+ s.strip()
            print(toDirectory)
            copy_tree(fromDirectory, toDirectory)
            print("moved")
        except:
            print(fromDirectory)

def fromFocusToPam():
    dirFocus="C://Users//claudio//Desktop//FOCUS//Round1//TestingInvocations//"
    dest = "C://Users//claudio//Desktop//folder1//TestingInvocations//"

    for f in os.listdir(dirFocus):
        file = open(dirFocus +f,"r",encoding='utf-8', errors='ignore')
        name=os.path.basename(file.name)
        lines=file.readlines()
        pam = open(dest + name, "w",encoding='utf-8', errors='ignore')
        try:
            for l in lines:
                cleaned=l.replace("#","','")
                cleaned=cleaned.replace("\n","'")
                #cleaned=cleaned.replace("$","")


                pam.write("'"+cleaned+"\n")
        except:
            continue

def groundT2():
    PAM_path = "C://Users//claudio//Desktop//PAM_processed//Round1//TestingInvocations//"
    total_row = 0
    for f in os.listdir(PAM_path):
        print(f)
        file = open(PAM_path + f, "r",encoding='utf-8', errors='ignore')

        lines = file.readlines()
        tot = len(lines)
        rmv = (tot * 2) / 3
        print(int(rmv))
        part = open("C://Users//claudio//Desktop//PAM_processed//Round1//TestingInvocations//" + f, "w",encoding='utf-8', errors='ignore')
        part2 = open("C://Users//claudio//Desktop//PAM_processed//Round1//GroundTruth//" + f, "w",encoding='utf-8', errors='ignore')
        i = 0
        c = 0
        for line in lines:
            part.write(line)
            i += 1
            if (i >= 3):
                break

        for line2 in lines:

            i += 1
            c += 1

            print(c)
            if (c >= 3):
                part2.write(line2)
                if (c >= tot):
                    break



def fixFormat():
    path= "C://Users//claudio//Desktop//PAM_processed//Round1//TestingInvocations//"
    pathRes="C://Users//claudio//Desktop//PAM_processed//Round1//cleaned//"
    for f in os.listdir(path):
        print(f)
        file = open(path + f, "r",encoding='utf-8', errors='ignore')
        result= open(pathRes + f, "w",encoding='utf-8', errors='ignore')
        i=0
        lines = file.readlines()
        for l in lines:
            l=l.replace("\n","',")
            result.write("'"+l)



def fix2():
    path = "C://Users//claudio//Desktop//PAM_processed//Round1//cleaned//"
    pathRes = "C://Users//claudio//Desktop//PAM_processed//Round1//TestingInvocations//"
    for f in os.listdir(path):
        print(f)
        file = open(path + f, "r", encoding='utf-8', errors='ignore')
        result = open(pathRes + f, "w", encoding='utf-8', errors='ignore')
        i = 0
        lines = file.readlines()
        for l in lines:
            l = l[:-1]
            result.write(l)


def mvFile():
    lpath2 = "C://Users//claudio//Desktop//PAM_all//"
    for f in os.listdir(lpath2):

        shutil.move(lpath2+f,"C://Users//claudio//Desktop//")
        break

#splitFiles()
#getMethodInv()
#groundT2()
#fixFormat()
#fix2()
#combineAll()
#retreiveList()
#findMalformed()
#movingFiles()
#fromFocusToPam()
#combineMethodsInv()
#renamingFile()

#combineAll("0b8674aeae24e0b2d731dd37f1bb8d2f80f6c088.arff")
mvFile()
