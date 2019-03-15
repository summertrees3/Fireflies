from tensorflow.examples.tutorials.mnist import input_data
import tensorflow as tf

mnist = input_data.read_data_sets('../data/MNIST_data/', one_hot=True)

print("训练集信息：")
print(mnist.train.images.shape, mnist.train.labels.shape)
print('验证集信息：')
print(mnist.validation.images.shape,mnist.validation.labels.shape)
print('测试集信息：')
print(mnist.test.images.shape,mnist.test.labels.shape)

# 实现模型 y=softmax(wx+b)
# placeholder：输入数据的地方，None代表不限条数的输入，每条是784维的向量
# Variable：存储模型参数，持久化
sess = tf.InteractiveSession()
x = tf.placeholder(tf.float32, [None, 784])
W=tf.Variable(tf.zeros([784, 10]))
b=tf.Variable(tf.zeros([10]))
y=tf.nn.softmax(tf.matmul(x, W)+b)

# 定义一个交叉熵作为loss函数cross_entropy，其中y是我们预测的概率分布， y_是实际的分布
y_=tf.placeholder(tf.float32, [None, 10])
# cross_entropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=y_, logits=y))
cross_entropy = tf.reduce_mean(-tf.reduce_sum(y_ * tf.log(y),reduction_indices=[1]))

# 采用随机梯度下降法，步长为0.5进行训练
train_step=tf.train.GradientDescentOptimizer(0.5).minimize(cross_entropy)
# 让模型循环训练1000次，每次随机train100条样本
tf.global_variables_initializer().run()
for i in range(1000):
    batch_xs, batch_ys=mnist.train.next_batch(100)
    train_step.run({x:batch_xs, y_:batch_ys})

# 模型评估
correct_prediction =tf.equal(tf.argmax(y,1), tf.argmax(y_,1))
accuracy=tf.reduce_mean(tf.cast(correct_prediction , tf.float32))
print("MNIST手写图片准确率：%g" % accuracy.eval({x: mnist.test.images, y_: mnist.test.labels}))

