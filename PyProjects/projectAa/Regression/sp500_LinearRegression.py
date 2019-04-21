import numpy as np
import pandas as pd
from sklearn.linear_model import LinearRegression, Ridge, RidgeCV
import sklearn.pipeline as pl
import sklearn.preprocessing as sp
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn import metrics
import matplotlib.pyplot as plt

sp500 = pd.read_csv("E:/useFiles/sp500.csv", usecols=[0, 4])
print(sp500.info())
# print(sp500.head(10))
next_day = sp500["Close"].iloc[1:]
# print(type(next_day))
sp500 = sp500.iloc[:-1, :]
sp500["next_day"] = next_day.values
# print(sp500.head(10))
# print(sp500.dtypes)


# 把数据分为70%训练集30%测试集
# np.random.seed(1)
# sp500 = sp500.loc[np.random.permutation(sp500.index)]
# highest_train_row = int(sp500.shape[0] * .7)
# train = sp500.loc[:highest_train_row, :]
# test = sp500.loc[highest_train_row:, :]
# train_x=train[["Close"]]
# train_y=train["next_day"]
# test_x=test[["Close"]]
# test_y=test["next_day"]
train_x,test_x,train_y,test_y = train_test_split(sp500[["Close"]],sp500[["next_day"]],test_size=0.3)
# print(type(train_x))


# 线性回归建模：基于梯度下降的最小二乘法
# regressor = LinearRegression()
# 均方误差: 82.1910857208317
# 均方根误差: 9.065929942418025
# 平均错误: 4.132879582938576


# 岭回归建模：增加正则项，以限制模型参数对异常样本的匹配程度，进而提高模型对多数正常样本的拟合精度
# regressor = Ridge(30, fit_intercept=True)


# 多项式回归建模：y=w0+w1x+w2x^2+w3x^3+...+wnx^n
regressor = pl.make_pipeline(sp.PolynomialFeatures(degree=2), LinearRegression())

# poly_features_2 = sp.PolynomialFeatures(degree=2, include_bias=False)
# poly_train_x_2 = poly_features_2.fit_transform(train_x)
# poly_test_x_2 = poly_features_2.fit_transform(test_x)
# regressor=LinearRegression()
# regressor.fit(poly_train_x_2, train_y.values.flatten())
# next_day_predictions = regressor.predict(poly_test_x_2)


# 随机森林：通过组合多颗决策树，能够降低模型的方差，与单颗决策树相比，随机森林通常具有更好的泛化性能。它对数据集中的异常值不敏感，不需要过多的参数调优。
# n_estimators:估计器(树)的个数，criterion:优化目标
# regressor = RandomForestRegressor(n_estimators=100,criterion="mse",n_jobs=1)


# 训练
# regressor.fit(train_x, train_y)
# flatten用于将多维数组降到1维，返回一份拷贝，对数据更改时不会影响原来的数组；而numpy.ravel()则返回视图，对数据更改时会影响原来的数组。
regressor.fit(train_x, train_y.values.flatten())
# 预测
next_day_predictions = regressor.predict(test_x)

# 模型评估
# mse = sum((next_day_predictions - test_y) ** 2) / len(next_day_predictions)
mse= metrics.mean_squared_error(test_y, next_day_predictions)
print("均方误差:", mse)
# rmse = np.sqrt(sum((next_day_predictions - test_y) ** 2) / len(next_day_predictions))
# print("均方根误差:", rmse)
# mae = sum(abs(next_day_predictions - test_y)) / len(next_day_predictions)
# print("平均错误:", mae)

# 可视化
plt.scatter(test_x, test_y)
plt.plot(test_x, next_day_predictions)
plt.show()

# 通常为了改善模型的精度我们会进行以下分析：
# 1.考虑更多的特征，使得模型更精确。比如，我们目前只是根据前一天的股价来预测目前的股价，更复杂的模型是根据前一个星期的股价来预测当前的股价。
# 2.考虑更多的模型，因为就目前而言，这个线性模型貌似不能很好的拟合我们的数据。
# 3.考虑预测更多的数据，比如我们预测未来一周的股票价格而不是未来一天的股票价格。
