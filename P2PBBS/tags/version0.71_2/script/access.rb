#指定したホストの指定したトピックにアクセスを繰り返す
#agrs[0] = ホストアドレス, [1] = ポート番号, [2] = 何回アクセスするか, [3] = カテゴリID, [4] = トピックID

require "net/http"
require "uri"

puts "ReadNodeAddress, ReadNodePort, numOfRead, CategoryID, TopicID"
if ARGV.size != 5
    exit
end

ReadNodeAddress = ARGV[0]
ReadNodePort = ARGV[1]
NumOfRead = ARGV[2].to_i
CategoryID = ARGV[3]
TopicID = ARGV[4]

def access(address, port, numofread,category,topic)
    begin
        response = Net::HTTP.get( address, "/command/read/" \
            + category +"/"+topic+"/0-0", port )
    rescue
        puts "Error"
    end
end

puts "\a"

NumOfRead.times{|i|
    puts "" + i.to_s + "回目のアクセス"
    startTime = Time.now.to_f
    access( ReadNodeAddress, ReadNodePort, NumOfRead, CategoryID, TopicID)
    endTime = Time.now.to_f
    execTime = endTime - startTime
    waitTime = 0.1 - execTime
    if( waitTime <= 0.0 ) then
        waitTime = 0.0
    end
    puts "start: " + startTime.to_s + " endTime: " + endTime.to_s + " wait:" + waitTime.to_s
    sleep waitTime
}

