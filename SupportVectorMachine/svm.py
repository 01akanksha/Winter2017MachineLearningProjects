#specify the path for data and algorithm for the kernel
data_path = 'C:/Users/A/Downloads/spambase/spambase.data'  # http://archive.ics.uci.edu/ml/datasets/Spambase
algorithm = 'svc_linear'

from numpy import genfromtxt
#Reading the dataset
print ("Reading the dataset:", data_path)
data = genfromtxt(data_path, delimiter=',') # no need to skip the header
n_samples = len(data)

from sklearn import preprocessing
#preprocessing and scalling the data on stadndard deviation and mean
features = preprocessing.scale(data[:, 0:-1])
target = data[:, -1]

from sklearn.cross_validation import train_test_split
#split the obtained data into two equal sets of training and testing data
X_train, X_test, y_train, y_test = train_test_split(
    features, target, test_size=0.5, random_state=17)

#**********************************************EXPERIMENT 1 ***********************************************************************************************
from sklearn.svm import SVC
#experiment 1 to train and test svm
print("Experment 1 results:")
def svc_linear(X_train, y_train):
    svc = SVC(kernel='linear').fit(X_train, y_train)
    return svc

training_algorithm = globals()[algorithm]
clf = training_algorithm(X_train, y_train)

# Make a prediction
y_pred = clf.predict(X_test)

from sklearn import metrics
#obtained results of experiment 1
print("Accuracy : \t",metrics.accuracy_score(y_test,y_pred))
print ("Precision: \t", metrics.precision_score(y_test, y_pred))
print ("Recall: \t", metrics.recall_score(y_test, y_pred))

from sklearn.metrics import roc_curve, auc
import matplotlib.pyplot as plt
import random
#valuea and the ROC curve plotting
y_score= clf.decision_function(X_test)
false_positive_rate, true_positive_rate, thresholds = roc_curve(y_test, y_score)
roc_auc = auc(false_positive_rate, true_positive_rate)
plt.title('Receiver Operating Characteristic')
plt.plot(false_positive_rate, true_positive_rate, 'b',
label='AUC = %0.2f'% roc_auc)
plt.legend(loc='lower right')
plt.plot([0,1],[0,1],'r--')
plt.xlim([-0.1,1.2])
plt.ylim([-0.1,1.2])
plt.ylabel('True Positive Rate')
plt.xlabel('False Positive Rate')
plt.show()
#**********************************************EXPERIMENT 2 ***********************************************************************************************
import heapq
import numpy as np

#to select m features according to the weights  vector select m with highest weights,train and test svm on the new data created based on the selected features
print("Experment 2 results:")
acc=[]
#genarating train and test data on the basis of the selected features and calculate accuracy on test data after training
for m in range(2,58):
    wt_vector=clf.coef_
    indices=heapq.nlargest(m, range(len(wt_vector[0])), abs(wt_vector[0]).take)
    if(m==5):
        print(indices)
    myList=[i for i in range(2300)]
    X_train_new=X_train[np.ix_(myList,indices)]
    myList1=[i1 for i1 in range(2301)]
    X_test_new=X_test[np.ix_(myList1,indices)]
    training_algorithm = globals()[algorithm]
    clf1 = training_algorithm(X_train_new, y_train)
    y_pred_new = clf1.predict(X_test_new)
    acc.append(metrics.accuracy_score(y_test,y_pred_new))
    print("Accuracy with ", m ,"features: \t", metrics.accuracy_score(y_test,y_pred_new))

import matplotlib.pyplot as plt
plt.plot(acc)
plt.ylabel('accuracy')
plt.xlabel('m(number of features selected)')
#plt.axis([2, 60, .5, 1])
plt.show()
#**********************************************EXPERIMENT 3 ***********************************************************************************************
#to select m features randomly and train and test svm on the new data created based on the selected features
#function to select m features randomly
def randindices(ind):
    indice=random.sample(range(0, 57), ind)
    return indice

print("Experment 3 results:")
acc1=[]
#calculating trainn and test data and accuracy on the test data
for m in range(2,58):
    b=randindices(m)
    myList2=[i2 for i2 in range(2300)]
    X_train_new1=X_train[np.ix_(myList2,b)]
    myList3=[i3 for i3 in range(2301)]
    X_test_new1=X_test[np.ix_(myList3,b)]
    training_algorithm = globals()[algorithm]
    clf2 = training_algorithm(X_train_new1, y_train)
    y_pred_new1 = clf2.predict(X_test_new1)
    acc1.append(metrics.accuracy_score(y_test,y_pred_new1))
    print("Accuracy with ", m ,"features: \t", metrics.accuracy_score(y_test,y_pred_new1))

plt.plot(acc1)
plt.ylabel('accuracy')
plt.xlabel('m(number of features selected)')
#plt.axis([2, 60, .5, 1])
plt.show()
