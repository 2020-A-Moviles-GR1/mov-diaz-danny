clc;
clear all;
close all;

id_functions = [15];
labels = [1];

X = [];
y = [];

X_augmentation = [];
y_augmentation = [];

user = 'user_test';


index_begin = 1;
index_end = 0;

index_augmentation_begin = 1;


for i=1:size(id_functions, 2)
  
  id_function = id_functions(i);
  
  filename = strcat(
    strcat("data,", 
      strcat(user, ",function")
    ), num2str(id_function)
  );
  extention = ".txt";
  fid = fopen(strcat("data/", strcat(filename, extention)));


  gesture_name = "gesture_uid_";



  ids = str2num(fgetl(fid));
  data_accelerometer_xs = str2num(fgetl(fid));
  data_accelerometer_ys = str2num(fgetl(fid));
  data_accelerometer_zs = str2num(fgetl(fid));
  data_gyroscope_xs = str2num(fgetl(fid));
  data_gyroscope_ys = str2num(fgetl(fid));
  data_gyroscope_zs = str2num(fgetl(fid));
  
  
  m = size(data_accelerometer_xs,1);
  
  fclose(fid);
  
  index_end += m;
  
  label = labels(i);
  
  X(index_begin:index_end, :) = [data_accelerometer_xs, data_accelerometer_ys, data_accelerometer_zs, data_gyroscope_xs, data_gyroscope_ys, data_gyroscope_zs];
  y(index_begin:index_end, 1) = label;
  
  index_begin += m;
  
  
  for index_sample=1:m  % por cada ejemplo
    
    
  
    original_data_accelerometer_x =  data_accelerometer_xs(index_sample, :); % 1x100    
    original_data_accelerometer_y =  data_accelerometer_ys(index_sample, :); % 1x100    
    original_data_accelerometer_z =  data_accelerometer_zs(index_sample, :); % 1x100    
    original_data_gyroscope_x =  data_gyroscope_xs(index_sample, :); % 1x100    
    original_data_gyroscope_y =  data_gyroscope_ys(index_sample, :); % 1x100    
    original_data_gyroscope_z =  data_gyroscope_zs(index_sample, :); % 1x100
 

    for forecast=3:15
      
      X_augmentation(index_augmentation_begin, :) = [ ...
        moving_average_box(original_data_accelerometer_x, forecast), ...
        moving_average_box(original_data_accelerometer_y, forecast), ...
        moving_average_box(original_data_accelerometer_z, forecast), ...
        moving_average_box(original_data_gyroscope_x, forecast), ...
        moving_average_box(original_data_gyroscope_y, forecast), ...
        moving_average_box(original_data_gyroscope_z, forecast)
        
      ];
      y_augmentation(index_augmentation_begin, 1) = label;
      index_augmentation_begin += 1;
    endfor
   
  endfor
  
  
  
  
  

  
endfor




