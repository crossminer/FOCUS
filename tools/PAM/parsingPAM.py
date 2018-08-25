import os


def splitFiles(PAM_path,part_path,part2_path):

    #PAM_path="C://Users//claudio//Desktop//dataset//"
    for f in os.listdir(PAM_path):
        print(f)
        file = open(PAM_path+f,"r",encoding='utf-8', errors='ignore')
        lines=file.readlines()
        tot=len(lines)
        rmv=(tot*2)/3
        print(int(rmv))
        part = open(part_path + f, "w",encoding='utf-8', errors='ignore')
        part2= open(part2_path + f, "w",encoding='utf-8', errors='ignore')
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



def combineMethodsInv(root_invocations,root_testing,root_arff):
    #root_processed="C://Users//claudio//Desktop//second_run//temp//"
    #root_testing="C://Users//claudio//Desktop//second_run//TestingInvocations//"
    #root_arff="C://Users//claudio//Desktop//second_run//Arff//"

    for f in os.listdir(root_invocations):
        file=open(root_invocations+f,"r",encoding='utf-8', errors='ignore')
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


def combineAll(start,end,dataset):
    #start="C://Users//claudio//Desktop//second_run//Arff//"
    #end="C://Users//claudio//Desktop//second_run//PAM_arff//"
    for file in os.listdir(start):
        ff =open(start+file,"r",encoding="utf-8",errors="ignore")
        resultArff=open(end+file,"w",encoding="utf-8",errors="ignore")
        for e in ff:
            resultArff.write(e)
        #folders="C://Users//claudio//Desktop//dataset//"
        for e in os.listdir(dataset):
            file=open(dataset+e,"r",encoding="utf-8",errors="ignore")
            for row in file:
                resultArff.write(row)



def fromFocusToPam(dirFocus,dest):
    #dirFocus="C://Users//claudio//Desktop//PAM_dataset//"
    #dest = "C://Users//claudio//Desktop//focusToPam//"

    for f in os.listdir(dirFocus):
        file = open(dirFocus +f,"r",encoding='utf-8', errors='ignore')
        name=os.path.basename(file.name)

        pam = open(dest + name, "w",encoding='utf-8', errors='ignore')
        for l in file:
            cleaned = l.replace("#", "','")
            cleaned = cleaned.replace("\n", "'")
            # cleaned=cleaned.replace("$","")
            pam.write("'" + cleaned + "\n")

def getGroundTruth(arff_path,invocations_path,groundt_path):
    #PAM_path = "C://Users//claudio//Desktop//focus_d//"
    total_row = 0
    for f in os.listdir(arff_path):
        print(f)
        file = open(arff_path + f, "r",encoding='utf-8', errors='ignore')
        lines = file.readlines()
        tot = len(lines)
        part = open(invocations_path + f, "w",encoding='utf-8', errors='ignore')
        part2 = open(groundt_path + f, "w",encoding='utf-8', errors='ignore')
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



def recommendationFormat(path,destPath):
    #path="C://Users//claudio//Desktop//second_run//Recommendations//"
    #destPath="C://Users//claudio//Desktop//PAM_recommendations//"
    for f in os.listdir(path):
        file=open(path+f,"r",encoding="utf-8",errors="ignore")
        dest=open(destPath+f,"w",encoding="utf-8",errors="ignore")
        list_prob=[]
        list_rec=[]
        dict={'rec': "", 'prob': ""}

        for e in file:
            pos2 = e.find("[")
            if pos2 != -1:
                rec = e[pos2 + 1:]
                rec = rec.replace("]", "")
                dict['rec'] =rec
                list_rec.append(dict["rec"])
            pos = e.find("prob:")
            if pos != -1:
                prob=e[pos+6:]
                dict['prob'] =prob
                list_prob.append(dict["prob"])

        for i,j in zip(list_rec,list_prob):
            dest.write(i.replace("\n","  "))
            dest.write(j)







