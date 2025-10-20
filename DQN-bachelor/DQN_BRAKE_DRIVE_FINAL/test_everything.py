import os
import random
from collections import deque
import numpy as np
import cv2
import time
from carla import Transform, Location, Rotation
import tensorflow as tf
from tensorflow.keras.models import load_model
from car_env import CarEnv, MEMORY_FRACTION
import carla
from carla import Transform, Location, Rotation
from agents.navigation.global_route_planner import GlobalRoutePlanner

# Trajectory 1
town2 = {1: [80, 306.6, 5, 0], 2: [135.25, 206]}

# Trajectory 2
town2 = {1: [-7.498, 284.716, 5, 90], 2: [81.98, 241.954]}

# To load the pretrained models for braking and driving
# Paths to the saved models
BASE_DIR = "/home/user/Safe-Navigation-Training-Autonomous-Vehicles-using-Deep-Reinforcement-Learning-in-CARLA/models/"
MODEL_PATH = BASE_DIR + "Braking___282.00max__282.00avg__282.00min__1679121006.h5"
MODEL_PATH2 = BASE_DIR + "Driving__6030.00max_6030.00avg_6030.00min__1679109656.h5"

model = load_model(MODEL_PATH)
model2 = load_model(MODEL_PATH2)
if __name__ == '__main__':
    # Set simulation parametersWDDWWAAWDDdsaS
    FPS = 60
    MEMORY_FRACTION = 0.5

    # Configure GPU memory growth
    gpus = tf.config.experimental.list_physical_devices('GPU')
    if gpus:
        try:
            for gpu in gpus:
                tf.config.experimental.set_virtual_device_configuration(
                    gpu,
                    [tf.config.experimental.VirtualDeviceConfiguration(memory_limit=MEMORY_FRACTION * 1024)])
        except RuntimeError as e:
            print(e)

    # Load the models
    model = load_model(MODEL_PATH)
    model2 = load_model(MODEL_PATH2)

    # Create environment
    env = CarEnv(town2[1], town2[2])

    # For agent speed measurements - keeps last 60 frametimes
    fps_counter = deque(maxlen=60)

    # Initialize predictions - first prediction takes longer due to initialization
    model.predict(np.array([[0, 0]]))
    model2.predict(np.array([[0, 0]]))

    # Loop over episodes
    for i in range(2):
        print('Restarting episode')

        # Reset environment and get initial state
        current_state = env.reset()
        env.collision_hist = []
        env.trajectory()
        done = False

        # Loop over steps
        while True:
            # For FPS counter
            step_start = time.time()

            # Traffic Lights
            if env.vehicle.is_at_traffic_light():
                if env.vehicle.get_traffic_light().get_state() == carla.TrafficLightState.Red:
                    print("Red")
                    action = 0
                    time.sleep(1 / FPS)
                else:
                    print("Green")
                    qs = model.predict(np.array(current_state[:2]).reshape(-1, *np.array(current_state[:2]).shape))[0]
                    action = np.argmax(qs)
                    if action == 1:
                        qs2 = model2.predict(np.array(current_state[2:]).reshape(-1, *np.array(current_state[2:]).shape))[0]
                        action = np.argmax(qs2) + 1
            else:
                # Predict an action based on current observation space
                qs = model.predict(np.array(current_state[:2]).reshape(-1, *np.array(current_state[:2]).shape))[0]
                action = np.argmax(qs)
                if action == 1:
                    qs2 = model2.predict(np.array(current_state[2:]).reshape(-1, *np.array(current_state[2:]).shape))[0]
                    action = np.argmax(qs2) + 1

            # Step environment
            new_state, reward, done, _ = env.step(action, current_state)

            # Set current step for next loop iteration
            current_state = new_state

            # If done - agent crashed, break an episode
            if done:
                break

            # Measure step time, append to a deque, then print mean FPS for last 60 frames, q values, and taken action
            frame_time = time.time() - step_start
            fps_counter.append(frame_time)
            print(f'Agent: {len(fps_counter) / sum(fps_counter):>4.1f} FPS | Action: [{qs[0]:>5.2f}, {qs[1]:>5.2f}] {action}')

        # Destroy actors at the end of the episode
        for actor in env.actor_list:
            actor.destroy()
# #####################################################################3333
# import os    
# import random
# from collections import deque
# import numpy as np
# import cv2
# import time
# import tensorflow as tf
# from tensorflow.keras import backend
# from tensorflow.keras.models import load_model
# from car_env import CarEnv, MEMORY_FRACTION
# import carla
# from carla import Transform, Location, Rotation
# from agents.navigation.global_route_planner import GlobalRoutePlanner

# # Trajectory 1
# town2 = {1: [80, 306.6, 5, 0], 2: [135.25, 206]}

# # Trajectory 2
# town2 = {1: [-7.498, 284.716, 5, 90], 2: [81.98, 241.954]}

# # To load the pretrained models for braking and driving
# # Paths to the saved models

# BASE_DIR = "/home/user/Safe-Navigation-Training-Autonomous-Vehicles-using-Deep-Reinforcement-Learning-in-CARLA/models/"

# MODEL_PATH = BASE_DIR + "Braking__282.00max282.00avg282.00min_1679121006.model"
# MODEL_PATH2 = BASE_DIR + "Driving_6030.00max_6030.00avg_6030.00min_1679109656.model"






 













# if __name__ == '__main__':
#     FPS = 60

#     # Memory fraction (e.g., 0.5 for 50% of GPU memory)
#     MEMORY_FRACTION = 0.5

#     # Configure GPU memory growth
#     gpus = tf.config.experimental.list_physical_devices('GPU')
#     if gpus:
#         try:
#             for gpu in gpus:
#                 tf.config.experimental.set_virtual_device_configuration(
#                     gpu,
#                     [tf.config.experimental.VirtualDeviceConfiguration(memory_limit=MEMORY_FRACTION * 1024)])
#         except RuntimeError as e:
#             print(e)

#     # Load the model
#     model = load_model(MODEL_PATH)
#     model2 = load_model(MODEL_PATH2)

#     # Create environment
#     env = CarEnv(town2[1], town2[2])

#     # For agent speed measurements - keeps last 60 frametimes
#     fps_counter = deque(maxlen=60)

#     # Initialize predictions - first prediction takes longer due to initialization
#     model.predict(np.array([[0, 0]]))
#     model2.predict(np.array([[0, 0]]))

#     # Loop over episodes
#     for i in range(2):
#         print('Restarting episode')

#         # Reset environment and get initial state
#         current_state = env.reset()
#         env.collision_hist = []
#         env.trajectory()
#         done = False

#         # Loop over steps
#         while True:
#             # For FPS counter
#             step_start = time.time()

#             # Traffic Lights
#             if env.vehicle.is_at_traffic_light():
#                 if env.vehicle.get_traffic_light().get_state() == carla.TrafficLightState.Red:
#                     print("Red")
#                     action = 0
#                     time.sleep(1 / FPS)
#                 else:
#                     print("Green")
#                     qs = model.predict(np.array(current_state[:2]).reshape(-1, *np.array(current_state[:2]).shape))[0]
#                     action = np.argmax(qs)
#                     if action == 1:
#                         qs2 = model2.predict(np.array(current_state[2:]).reshape(-1, *np.array(current_state[2:]).shape))[0]
#                         action = np.argmax(qs2) + 1
#             else:
#                 # Predict an action based on current observation space
#                 qs = model.predict(np.array(current_state[:2]).reshape(-1, *np.array(current_state[:2]).shape))[0]
#                 action = np.argmax(qs)
#                 if action == 1:
#                     qs2 = model2.predict(np.array(current_state[2:]).reshape(-1, *np.array(current_state[2:]).shape))[0]
#                     action = np.argmax(qs2) + 1

#             # Step environment
#             new_state, reward, done, _ = env.step(action, current_state)

#             # Set current step for next loop iteration
#             current_state = new_state

#             # If done - agent crashed, break an episode
#             if done:
#                 break

#             # Measure step time, append to a deque, then print mean FPS for last 60 frames, q values, and taken action
#             frame_time = time.time() - step_start
#             fps_counter.append(frame_time)
#             print(f'Agent: {len(fps_counter) / sum(fps_counter):>4.1f} FPS | Action: [{qs[0]:>5.2f}, {qs[1]:>5.2f}] {action}')

#         # Destroy actors at the end of the episode
#         for actor in env.actor_list:
#             actor.destroy()