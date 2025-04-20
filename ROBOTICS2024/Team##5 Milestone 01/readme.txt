\documentclass[10pt, conference, compsocconf]{IEEEtran}
%%\input{Sections/packages}  %Documenting start here...
\usepackage{graphicx}
\usepackage{float}
\begin{document}

%%\input{Sections/Title_and_Authors}

%%\input{Sections/Abstract}

% Section 1: Introduction
\section{Introduction}
Industrial robots have increasingly become integral to manufacturing processes due to their ability to automate repetitive tasks, improve precision, and enhance productivity. SCARA (Selective Compliance Assembly Robot Arm) systems are widely used for tasks requiring speed and precision in a two-dimensional plane. This literature review summarizes recent research in SCARA robotic arm systems and their applications, followed by a proposed design for an automated 2D cutting and product handling system tailored to SMEs.

% Section 2: Literature Review
\section{Literature Review}

\subsection{Low-Cost SCARA Robotic Arm for MSMEs}
A study by \textit{Author et al.} (2015) proposed a cost-effective SCARA robotic arm aimed at micro and small manufacturing enterprises (MSMEs). The system integrates CNC technology with a SCARA arm controlled by an Arduino micro-controller, offering tasks such as drilling through touchscreen inputs. This research addresses the financial barriers MSMEs face in acquiring traditional CNC systems by combining precision and affordability, making advanced manufacturing technology accessible to smaller businesses \cite{author2015}.

\subsection{Challenges in Industrial Robotics for Machining Operations}
Machining operations using industrial robots, particularly with hard materials such as steel, present significant challenges due to the mechanical loads involved. \textit{Author et al.} (2017) introduced a mechanistic model to predict cutting forces during milling operations. The study explored how factors such as tool geometry, material resistance, and cutting thickness affect the mechanical stresses on the robotic arm, providing valuable insights for designing robots capable of performing in high-load environments \cite{author2017}.

\subsection{Robotic Cutting Technologies: A Review}
A review by \textit{Author et al.} (2018) examined various robotic cutting technologies, including water-jet, laser, ultrasonic, plasma, and oxy-gas cutting. Each method was analyzed based on its suitability for different materials and industries. The study highlighted the growth potential of robotic cutting technologies in industries such as automotive, food processing, and medical surgery, emphasizing the versatility of robots in adapting to a wide range of industrial applications \cite{author2018}.

\subsection{Design and Testing of a 3-DOF SCARA Robot for Pick-and-Place Operations}
\textit{Author et al.} (2020) presented the design and testing of a 3-degree-of-freedom (DOF) SCARA robotic arm configured for pick-and-place tasks. The robot features two rotational joints and one prismatic joint, enabling accurate and fast material handling. Controlled by an Arduino-based system, the robot is equipped with a MATLAB-based graphical user interface (GUI) that converts Cartesian coordinates into angular positions. Future improvements suggested include advanced control methods, such as vision systems and gesture recognition \cite{author2020}.

\subsection{4-DOF SCARA Robotic Arm for Educational and Industrial Applications}
The development of a 4-DOF SCARA robotic arm for both educational and industrial purposes was explored by \textit{Author et al.} (2022). The design emphasizes affordability and flexibility, using an Arduino controller with Bluetooth interfacing. The robot's mechanical integrity was ensured through stress analysis using CREO and ANSYS software. The arm is suitable for assembly tasks in industrial environments while being accessible for educational purposes due to its low cost and ease of assembly \cite{author2022}. 

% Section 3: Proposed Industrial Application
\section{Proposed Industrial Application: SCARA Robotic Arm for 2D Cutting and Product Handling}

\subsection{Overview}
Based on the reviewed literature, the proposal is an industrial application involving a 3-DOF SCARA robotic arm designed for cutting 2D shapes on cardboard sheets. The system will incorporate two rotational joints for planar movement and a prismatic joint for vertical motion, allowing for precise cutting in depth. A secondary robotic arm will handle the products post-cutting, ensuring a continuous and automated workflow suitable for small and medium enterprises (SMEs). The proposed model is shown in figure 1
\begin{figure}[H]
    \centering
    \includegraphics[width=0.7\linewidth]{WhatsApp Image 2024-09-19 at 21.06.21_47455366.jpg}
    \caption{Proposed SCARA robot model}
    \label{fig:your_label}
\end{figure}


\subsection{System Design}

\subsubsection{SCARA Robotic Arm for 2D Cutting}
The SCARA robotic arm will operate on two rotational joints to perform movements in the X-Y plane, while the prismatic joint will enable vertical cutting adjustments. Controlled by an Arduino micro-controller, the robot will precisely cut shapes or draw patterns on cardboard sheets or similar materials. The depth of the cut will be managed by adjusting the prismatic joint's motion.

\subsubsection{Secondary Robotic Arm for Product Handling}
Once the SCARA arm completes the cutting operation, a secondary robotic arm will be used to pick up the cut products and place them in a designated rack. This arm will work in coordination with the SCARA arm, maintaining a continuous flow of production. The coordination between the two arms will be achieved by establishing a shared reference point between their respective world frames, which will be centered on the cut product.

\subsection{Workflow and Methodology}
This section describes the basic workflow for completing the SCARA Robot project. It consists of the following steps
\begin{enumerate}
    \item \textbf{Basic information about robot:} Before delving into anything, the basic information and components of the SCARA robot are discussed, including number of DOF, number of links, and the end-effector shape.
    \item \textbf{List of Components:} After deciding all that, its crucial to first build a list of components and a billing list. All different components are considered, from the smallest joint to the largest link. This will be crucial in assembling the robot and 3D printing it later on. The List of Components is shown below.
    \begin{itemize}
        \item 1 nema 17 4 pieces
        \item 2 cables 4 pieces
        \item 3 limit swtch 4 pieces
        \item 4 power 10a 1 piece
        \item 5 power cable 1 piece
        \item 6 uno 1 piece
        \item 7 uno shield 1 piece
        \item 8 a4988 4 piece
        \item 9 power plug 1 piece
        \item 10 wires 10 m
        \item 11 servo mg995 1
        \item Parts no off
        \item 3d printed 1245
        \item gripper 90
        \item Smooth rod 10mm 1m 2
        \item Lead screw 0.5
        \item Lead screw nut 1
        \item Linear bearings 10mm lm10uu 4
        \item (51108)Thrust ball bearing 40x60x13mm 2
        \item (51107)Thrust ball bearing 35x52x12mm 2
        \item (608) ball bearing 8x22x7mm 5
        \item 6807 ball bearing 35*47 1
        \item 6806 ball bearing 30*42 1
        \item Belt 200mm 1
        \item Belt 300mm 2
        \item belt 400mm 2
        \item Coupler 5*8 1
        \item pulley no teeth 7
        \item m3*16 5
        \item m3*12 20
        \item3*20 10
        \item m3*25 5
        \item m3*30 10
        \item m5*355
        \item m5*305
        \item m4*15 10
        \item m4*25 8
        \item m4*20 40
        \item m4*45 10
        \item m4*40 10
        \item m8*45 2
        \item linear guive m8 2*25 1
    \end{itemize}
    \item \textbf{SolidWorks model:} The SolidWorks model is crucial, because it allows us to assemble the components listed in the List of Components, as well as give the ability to utilize different robotic simulation software such as Simscape Multibody and Coppeliasim by linking the SolidWorks file with them. The CAD model for SCARA Robot is shown in figure 2.
    \begin{figure}[H]
        \centering
        \includegraphics[width=1\linewidth]{SCARA robot.png}
        \caption{SCARA Robot SolidWorks CAD model}
        \label{fig:enter-label}
    \end{figure}
    \item \textbf{Robot Simulation:} First the D-H convention for the entire robot is designed, then after linking the SolidWorks file with either Simscape Multibody or Coppeliasim for high-accuracy, the robot's movement is simulated. The electrical connections are designed with Proteus software. Forward kinematic analysis are performed, MATLAB-coded, and simulated using one of the simulation software.
    The Simscape Multibody model is shown in figure 3
    \begin{figure}[H]
        \centering
        \includegraphics[width=0.8\linewidth]{WhatsApp Image 2024-10-02 at 21.49.25_8144db11.jpg}
        \caption{Simscape Multibody model}
        \label{fig:your_label111}
    \end{figure}
    The Coppeliasim model is shown in figure 4.
    \begin{figure}[H]
        \centering
        \includegraphics[width=0.8\linewidth]{WhatsApp Image 2024-10-02 at 22.10.30_e74cfa4d.jpg}
        \caption{Coppeliasim model}
        \label{fig:your_label1}
    \end{figure}
    \item \textbf{Fabrication and Testing:} After the simulation part is completed, the robot is now ready for prototyping and fabrication. The SolidWorks CAD model along wit the List of Components are provided, then the relevant parts are 3D printed. The servo motors used at each joint will be purchased according to the specifications shown in the List of Components.
\end{enumerate}

% Section 4: Conclusion
\section{Conclusion}
This literature review highlighted recent advancements in SCARA robotic arms and their industrial applications. The proposed system integrates a 3-DOF SCARA arm for 2D cutting and a secondary arm for product handling, providing an automated solution for small and medium enterprises. This approach leverages the latest technological trends, offering a cost-effective, scalable, and efficient system for automating repetitive tasks in manufacturing environments.\\
The methodology of the implementation of the SCARA robot was also shown, where it included using the CAD model alongside list of components, as well as as different simulation softwares like Simscape Multibody and Coppeliasim to ensure accurate and high-quality analysis before the final fabrication and hardware testing.

% Bibliography
\bibliographystyle{IEEEtran}

\begin{thebibliography}{9}
\bibitem{author2015} Author et al., "Low-Cost SCARA Robotic Arm for MSMEs," \textit{Journal of Industrial Automation}, 2015.
\bibitem{author2017} Author et al., "Challenges in Using Industrial Robots for Machining Operations," \textit{International Journal of Robotics Research}, 2017.
\bibitem{author2018} Author et al., "Robotic Cutting Technologies: A Review," \textit{Journal of Manufacturing Science}, 2018.
\bibitem{author2020} Author et al., "Design and Testing of a 3-DOF SCARA Robot for Pick-and-Place Operations," \textit{Automation and Robotics Journal}, 2020.
\bibitem{author2022} Author et al., "4-DOF SCARA Robotic Arm for Educational and Industrial Applications," \textit{Robotics Engineering Review}, 2022.
\end{thebibliography}
\end{document}