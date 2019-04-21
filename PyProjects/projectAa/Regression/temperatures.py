import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
import matplotlib.pyplot as plt


# 数据读取与处理
df=pd.read_csv("E:/useFiles/temperatures.csv", error_bad_lines=False, names=['date', 'temp'], header=0)
# df.drop(df.index[[565,566,1290]],inplace=True)
df["temp"]=pd.to_numeric(df["temp"], errors ='coerce')      #将温度转换为float类型，无法转的置为NaN
df.dropna(axis=0,how='any',inplace=True)                    #删除行里有NaN的行
df.set_index("date", inplace=True)
# df.index = df['date']

next_one = df["temp"].iloc[1:]
next_two = df["temp"].iloc[2:]
# print(type(next_temp))
df = df.iloc[:-1, :]
df["next_one"] = next_one.values
df = df.iloc[:-1, :]
df["next_two"] = next_two.values


print(df.info())
# print(df.describe())
print(df[:5])


train_x,test_x,train_y,test_y = train_test_split(df[['temp', 'next_one']], df[['next_two']], test_size=0.3, random_state=12)
regressor=LinearRegression()
regressor.fit(train_x, train_y)
pred=regressor.predict(test_x)
print(test_y.head(5))
print(pred[:5])


# fig, ax = plt.subplots()
# ax.scatter(test_y, pred)
# ax.plot([y.min(), y.max()], [y.min(), y.max()], 'k--', lw=4)
# ax.set_ylabel('Measured')
# ax.set_xlabel('Predicted')
# plt.show()

# plt.scatter(test_x, test_y)
# plt.plot(test_y, pred)
# plt.show()