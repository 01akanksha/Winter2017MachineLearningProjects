data_path = 'C:/Users/A/Downloads/spambase/spambase.data'
import numpy as np
from numpy import genfromtxt
#Reading and shuffling the dataset
print ("Reading the dataset:", data_path)
data = genfromtxt(data_path, delimiter=',') # no need to skip the header
n_samples = len(data)
target = data[:, -1]
np.random.shuffle(data)

from sklearn import preprocessing
#preprocessing and scalling the data on stadndard deviation and mean
features = preprocessing.scale(data[:, 0:-1])
target = data[:, -1]

from sklearn.cross_validation import train_test_split
#split the obtained data into two equal sets of training and testing data
X_train, X_test, y_train, y_test = train_test_split(
    features, target, test_size=0.5, random_state=17)

#Calculate the probabilities for the spam and not spam classes
spamCount = 0
trainlength = len(X_train)
for row in range(trainlength):
    if y_train[row] == 1:
        spamCount += 1
pspam = spamCount / trainlength
pnotspam = 1 - pspam
print("Probability of spam: \t",pspam)
print("Probability of not-spam: \t",pnotspam)

#Calculating mean and Standard Deviation for features
smean,sdev,nsmean,nsdev  = [], [],[],[]
for features in range(0,57):
    scol,nscol = [],[]
    for row in range(0, trainlength):
        if (y_train[row] == 1):
            scol.append(X_train[row][features])
        else :
           nscol.append(X_train[row][features])
    smean.append(np.mean(scol))
    nsmean.append(np.mean(nscol))
    sdev.append(np.std(scol))
    nsdev.append(np.std(nscol))

#checking for zero and replacing with .0001
for feature in range(0,57):
    if(sdev[feature]==0):
        sdev[feature] = .0001
    if(nsdev[feature]==0):
        nsdev[feature]=.001

#Calculate precision, Recall and accuracy
def CalResults(target, scoreValues, threshold):
    truePositives,falsePositives,trueNegatives,falseNegatives = 0,0,0,0
    for row in range(len(scoreValues)):
        if (scoreValues[row] > threshold and target[row] == 1)  :
            truePositives += 1
        elif (scoreValues[row] > threshold and target[row] == 0 )  :
            falsePositives += 1
        elif (scoreValues[row] <= threshold and target[row] == 1 )  :
            falseNegatives += 1
        elif (scoreValues[row] <= threshold and target[row] == 0 )  :
            trueNegatives += 1
    accuracy = float(truePositives + trueNegatives) / len(scoreValues)
    recall = float(truePositives) / (truePositives + falseNegatives)
    precision = float(truePositives) / (truePositives + falsePositives)
    return  accuracy, recall, precision

# Calculating probabilities for spam and not spam for each example and predicting the ouput class
sprob,nsprob = 0,0
predicted = []
for rows in range(0,len(X_test)):
    finalspam,finalnotspam = [],[]
    partCal1,partCal2,partCal3,partCal4 = 0,0,0,0
    for features in range(0,57):
        partCal1 = float(1)/ (np.sqrt(2 * np.pi) * sdev[features])
        partCal2 = (np.e) ** - (((X_test[rows][features] - smean[features]) ** 2) / (2 * sdev[features] ** 2))
        finalspam.append(partCal1 * partCal2)
        partCal3 = float(1)/ (np.sqrt(2 * np.pi) * nsdev[features])
        partCal4 = (np.e) ** - (((X_test[rows][features] - nsmean[features]) ** 2) / (2 * nsdev[features] ** 2))
        finalnotspam.append(partCal3 * partCal4)
        #if(finalspam[features]==0):
        #    finalspam[features]=10**-10
        #if(finalnotspam[features]==0):
        #    finalnotspam[features]=10**-10
    sprob = np.log(pspam) + np.sum(np.log(np.asarray(finalspam)))
    nsprob = np.log(pnotspam) + np.sum(np.log(np.asarray(finalnotspam)))
    output = np.argmax([nsprob, sprob])
    predicted.append(output)

from sklearn import metrics
#Results accuracy, recall, precision and confusion matrix
a,r,p = CalResults(y_test, predicted, 0)
print("Confusion matrix:\n",metrics.confusion_matrix(y_test, predicted))
print ("Accuracy: \t",a)
print ("Precision: \t", p)
print ("Recall: \t",r)
#*****************************************************************************PART 2*******************************************************************************************
import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn import metrics
#Logistic Regression training and testing
model = LogisticRegression()
model = model.fit(X_train, y_train)
predicted = model.predict(X_test)
probs = model.predict_proba(X_test)
print(model.get_params())
print(model)
#Results accuracy, recall, precision and confusion matrix
print ("Accuracy: \t",metrics.accuracy_score(predicted,y_test))
print ("Precision: \t", metrics.precision_score(y_test, predicted))
print ("Recall: \t", metrics.recall_score(y_test, predicted))
print ("Confusion Matrix: \n",metrics.confusion_matrix(y_test, predicted))
