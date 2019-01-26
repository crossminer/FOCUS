import os
import sys

from pygments.lexer import default


def run(PAM_INPUT_RESULTS, PAM_OUTPUT_RESULT):
    createFolder(default + "temp")
    for f in os.listdir(PAM_INPUT_RESULTS):
        file=open(os.path.join(PAM_INPUT_RESULTS, f), "r", encoding="utf-8", errors="ignore")
        dest=open(os.path.join(PAM_OUTPUT_RESULT + "temp", f), "w", encoding="utf-8", errors="ignore")
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
    fixBugs(PAM_OUTPUT_RESULT)


def fixBugs(path):
    destPath = path
    path = path + "temp"
    for f in os.listdir(path):
        file = open(path + f, "r", encoding="utf-8", errors="ignore")
        dest = open(destPath + f, "w", encoding="utf-8", errors="ignore")
        for e in file:
            num = e.count("(")
            num2 = e.count(")")
            # caso 1: parentesi aperta
            if num == 1 and num2 == 0:
                pos = e.find("(")
                next = e[pos + 1:pos + 2]
                if next == " ":
                    first = e[:pos]
                    last = e[pos + 1:]
                    dest.write(first + "()" + last)
                    print(last)
                else:
                    pos3 = e.find("0")
                    arg = e[pos + 1:pos3 - 1]
                    arg = arg.replace(" ", "")
                    print(arg)
                    dest.write(first + "(" + arg + ")" + last)
                    # caso 2 parentesi chiusa
            if num == 0 and num2 == 1:
                pos = e.find(")")
                e = e.replace(")", "")
                dest.write(e)
            if num == 1 and num2 == 1:
                dest.write(e)
            if num == 2 and num2 == 2:
                dest.write(e)
            if num == 0 and num2 == 0:
                dest.write(e)

def createFolder(myPath):
    if not os.path.exists(myPath):
        os.makedirs(myPath)

if __name__ == "__main__":
    PAM_INPUT_RESULTS = sys.argv[1]
    PAM_OUTPUT_RESULT = sys.argv[2]
    run(PAM_INPUT_RESULTS, PAM_OUTPUT_RESULT)
