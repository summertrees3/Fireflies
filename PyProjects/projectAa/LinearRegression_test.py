import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from sklearn import datasets, linear_model, metrics
from sklearn.cross_validation import train_test_split
from sklearn.linear_model import LinearRegression
from sklearn.model_selection import cross_val_predict


data = pd.read_csv('E:/useFiles/CCPP/Folds5x2_pp.csv')
# print(type(data))
print(data[:3])
# print(data.shape)
train_test_split()
X = data[['AT', 'V', 'AP', 'RH']]
y = data[['PE']]
# 训练集和测试集划分
X_train, X_test, y_train, y_test = train_test_split(X, y, random_state=1)
# print(X_train.shape)
# print(y_train.shape)
# 训练样本数据
linreg = LinearRegression()
linreg.fit(X_train, y_train)
print(linreg.intercept_)
print(linreg.coef_)

y_pred = linreg.predict(X_test)
print("MSE：", metrics.mean_squared_error(y_test, y_pred))
print("RMSE：", np.sqrt(metrics.mean_squared_error(y_test, y_pred)))

# 交叉验证
predicted = cross_val_predict(linreg, X, y, cv=10)
print("交叉MSE：", metrics.mean_squared_error(y, predicted))
print("交叉RMSE：", np.sqrt(metrics.mean_squared_error(y, predicted)))

# 画图
fig, ax = plt.subplots()
ax.scatter(y, predicted)
ax.plot([y.min(), y.max()], [y.min(), y.max()], 'k--', lw=4)
ax.set_xlabel('Measured')
ax.set_xlabel('Predicted')
plt.show()

