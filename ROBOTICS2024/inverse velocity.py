def inverse_velocity(q, X_dot):
    """
    Calculate joint velocities given the desired end-effector velocity.
    
    Parameters:
        q (array): Joint angles [q1, q2, q3].
        X_dot (array): Desired end-effector velocity [Vx, Vy, Vz].
    
    Returns:
        np.ndarray: Joint velocities [q1_dot, q2_dot, q3_dot].
    """
    # Obtain the Jacobian matrix for the current joint configuration
    J = jacobian_matrix(q)

    # Check the rank of the Jacobian to decide if it's invertible
    if np.linalg.matrix_rank(J) == 3:
        # Direct inverse if the Jacobian is full rank
        J_inv = np.linalg.inv(J)
    else:
        # Use pseudo-inverse for singular configurations
        J_inv = np.linalg.pinv(J)

    # Compute joint velocities (q_dot) by multiplying the Jacobian inverse with the desired end-effector velocity
    q_dot = np.dot(J_inv, X_dot)  # q_dot = J_inv * X_dot
    
    return q_dot