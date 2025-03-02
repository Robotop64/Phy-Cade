// #pragma once

// #include <chrono>
// #include <vector>
// #include <cstring>

// namespace Profiler
// {
//     using Time = std::chrono::time_point<std::chrono::high_resolution_clock>;
//     using Duration = std::chrono::duration<double>;

//     struct TimeStamp
//     {
//         Time time;
//         TimeStampConfig config;
//     };

//     struct TimeStampConfig
//     {
//         char *id;
//         char *target_id; // who the TimeStamp should be relative to
//     };

//     struct Data
//     {
//         std::vector<TimeStamp> time_stamps;
//         std::vector<size_t> target_indices;
//         std::vector<Duration> time_deltas;
//         bool can_grow;
//         size_t time_stamp_index;
//     };
//     static Data data;

//     static void Init()
//     {
//         data.time_stamps = std::vector<TimeStamp>();
//         data.can_grow = true;
//         data.time_stamp_index = 0;
//     };

//     static void SetTimeStamp(TimeStampConfig config)
//     {
//         Time t = std::chrono::high_resolution_clock::now();
//         TimeStamp ts = {t, config};

//         if (data.can_grow)
//         {
//             data.time_stamps.push_back(ts);
//         }
//         else
//         {
//             data.time_stamps[data.time_stamp_index].time = t;
//             data.time_stamp_index++;
//         }
//     };

//     void calc_indices()
//     {
//         data.target_indices = std::vector<size_t>(data.time_stamps.size());
//         data.time_deltas = std::vector<Duration>(data.time_stamps.size());

//         for (size_t i = 0; i < data.time_stamps.size(); i++)
//         {
//             TimeStamp ts = data.time_stamps[i];
//             TimeStampConfig config = ts.config;

//             if (config.target_id != nullptr)
//             {
//                 for (size_t j = 0; j < data.time_stamps.size(); j++)
//                 {
//                     TimeStamp ts2 = data.time_stamps[j];
//                     TimeStampConfig config2 = ts2.config;

//                     if (strcmp(config.target_id, config2.id) == 0)
//                     {
//                         data.target_indices[i] = j;
//                     }
//                 }
//             }
//             else
//             {
//                 data.target_indices[i] = 0;
//             }
//         }
//     }

//     static void close()
//     {
//         if (data.can_grow)
//         {
//             calc_indices();
//             data.can_grow = false;
//         }
//         deltas();
//     };

//     void deltas()
//     {
//         for (size_t i = 0; i < data.time_stamps.size(); i++)
//         {
//             TimeStamp ts = data.time_stamps[i];
//             TimeStampConfig config = ts.config;

//             if (config.target_id != nullptr)
//             {
//                 size_t target_index = data.target_indices[i];
//                 TimeStamp target_ts = data.time_stamps[target_index];

//                 std::chrono::duration<double> delta = ts.time - target_ts.time;
//                 data.time_deltas[i] = delta;
//             }
//         }

//         data.time_stamp_index = 0;
//     };

//     static Duration get_delta(char *id)
//     {
//         for (size_t i = 0; i < data.time_stamps.size(); i++)
//         {
//             TimeStamp ts = data.time_stamps[i];
//             TimeStampConfig config = ts.config;

//             if (strcmp(config.id, id) == 0)
//             {
//                 return data.time_deltas[i];
//             }
//         }

//         return Duration(0);
//     };
// };
