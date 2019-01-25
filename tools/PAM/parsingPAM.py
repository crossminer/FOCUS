import os
import sys
import ConvertArffFile



def splitFiles(PAM_path,part_path,part2_path):

    #PAM_path="C://Users//claudio//Desktop//dataset//"
    files = (file for file in os.listdir(PAM_path)
             if os.path.isfile(os.path.join(PAM_path, file)))
    for f in files:

        file = open(PAM_path+f,"r",encoding='utf-8', errors='ignore')
        lines=file.readlines()
        tot=len(lines)
        rmv=(tot*2)/3

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

        file2=open(os.path.join(root_testing, f),"r",encoding='utf-8', errors='ignore')

        result_arff = open(os.path.join(root_arff,f),"w", encoding='utf-8', errors='ignore')
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
        resultArff=open(os.path.join(end,file),"w",encoding="utf-8",errors="ignore")
        for e in ff:
            resultArff.write(e)
        #folders="C://Users//claudio//Desktop//dataset//"
        for e in os.listdir(dataset):
            if os.path.isfile(os.path.join(dataset, e)):
                file=open(os.path.join(dataset, e),"r",encoding="utf-8",errors="ignore")
                for row in file:
                    resultArff.write(row)



def fromFocusToPam(dirFocus,dest):
    #dirFocus="C://Users//claudio//Desktop//PAM_dataset//"
    #dest = "C://Users//claudio//Desktop//focusToPam//"
    files = (file for file in os.listdir(dirFocus)
             if os.path.isfile(os.path.join(dirFocus, file)))
    for f in files:
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
        if os.path.isfile(os.path.join(arff_path,f)):
            file = open(os.path.join(arff_path,f), "r",encoding='utf-8', errors='ignore')
            lines = file.readlines()
            tot = len(lines)
            part = open(os.path.join(invocations_path,f), "w",encoding='utf-8', errors='ignore')
            part2 = open(os.path.join(groundt_path,f), "w",encoding='utf-8', errors='ignore')
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
#focus_path = '/home/juri/git/FOCUS/dataset/SH_L/'
#pam_path = '/home/juri/Scrivania/output/PAM/'
#fromFocusToPam(focus_path, pam_path)
#splitFiles(pam_path, '/home/juri/Scrivania/output/2_3/', '/home/juri/Scrivania/output/1_3/')

def removeTempFiles(pam_path):
    files = (file for file in os.listdir(pam_path)
             if file.endswith(".txt"))
    for f in files:
        os.remove(os.path.join(pam_path, f))

def cleanFolder(myPath):
    for root, dirs, files in os.walk(myPath):
        for file in files:
            os.remove(os.path.join(root, file))

def createFolder(myPath):
    if not os.path.exists(myPath):
        os.makedirs(myPath)

def run(focus_path,pam_path):
    cleanFolder(pam_path)
    createFolder(os.path.join(pam_path,'2_3/'))
    createFolder(os.path.join(pam_path, '1_3/'))
    createFolder(os.path.join(pam_path, 'RP/'))
    createFolder(os.path.join(pam_path, 'GT/'))
    createFolder(os.path.join(pam_path, 'RESULT/'))
    createFolder(os.path.join(pam_path, 'DESTINATION/'))
    print("Generating temp files")
    fromFocusToPam(focus_path, pam_path)
    print('Converting to arff format')
    ConvertArffFile.run(pam_path,pam_path)
    print('Remove temp files')
    removeTempFiles(pam_path)
    print ('Split dataset in 1/3 and 2/3 partitions')
    splitFiles(pam_path, os.path.join(pam_path,'2_3/'), os.path.join(pam_path,'1_3/'))
    print ('Compute GT')
    getGroundTruth(pam_path, os.path.join(pam_path,'RP/'), os.path.join(pam_path,'GT/'))
    combineMethodsInv(os.path.join(pam_path,'2_3/'), os.path.join(pam_path,'RP/'), os.path.join(pam_path, "RESULT/"))
    combineAll(os.path.join(pam_path,"RESULT/"), os.path.join(pam_path,"DESTINATION/"),pam_path)

if __name__ == "__main__":
    focus_path = '/home/juri/Scrivania/input/'#sys.argv[1]
    pam_path = '/home/juri/Scrivania/output/PAM/'#sys.argv[2]
    run(focus_path, pam_path)

