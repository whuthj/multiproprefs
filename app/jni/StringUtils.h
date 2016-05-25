#pragma once

#include <string>

class StringUtils
{
public:
    static void Split(const std::string& sSrc, const std::string& sSplitStr, std::vector<std::string>& vecOutput, bool bIncludeEmpty)
    {
        vecOutput.clear();
        std::string tmp_str;
        int pos_find = 0;
        int pos_begin = 0;
        while (pos_find != std::string::npos)
        {
            pos_find = sSrc.find(sSplitStr, pos_begin);
            if (pos_find != std::string::npos)
                tmp_str = sSrc.substr(pos_begin, pos_find - pos_begin);
            else
                tmp_str = sSrc.substr(pos_begin);
            if (bIncludeEmpty || tmp_str.size() > 0)
                vecOutput.push_back(tmp_str);
            pos_begin = pos_find + sSplitStr.size();
        }
    }
};